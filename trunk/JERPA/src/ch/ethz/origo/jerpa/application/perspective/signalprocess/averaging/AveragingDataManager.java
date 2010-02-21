package ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.MathException;

import ch.ethz.origo.jerpa.application.exception.CorruptedFileException;
import ch.ethz.origo.jerpa.application.exception.InsufficientDataException;
import ch.ethz.origo.jerpa.application.exception.InvalidFrameIndexException;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalProject;
import ch.ethz.origo.jerpa.data.Buffer;
import ch.ethz.origo.jerpa.data.Epoch;
import ch.ethz.origo.jerpa.data.Header;
import ch.ethz.origo.jerpa.data.IndexesLoader;

/**
 * Programov� rozhran� pro p��stup aplika�n� vrstvy ��sti <i>pr�m�rov�n� epoch a
 * export v�sledk�</i> ke instanci (v�dy jen k jedn�) t��dy
 * <code>Project</code>. Jedn� se o stavebn� k�men aplika�n� vrstvy zm�n�n�
 * ��sti. Na z�klad� po�adavk� prezenta�n� vrstvy prov�d� nad <i>projektem</i>
 * takov� operace, aby z�stala zachov�na integrita dat z pohledu pr�m�rov�n�
 * epoch. D�le poskytuje data pro zobrazen� pro <code>AveragingWindow</code> a
 * <code>ExportFrame</code>.
 * 
 * @author Tomas Rondik (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0 (11/18/09)
 * 
 */
public class AveragingDataManager {
	/**
	 * Reference na projekt, se kter�m u�ivatel pr�v� pracuje.
	 */
	private SignalProject project;
	/**
	 * Konstanta zp�sobu pr�ce se skupinou epoch - pr�ce se v�emi epochami.
	 */
	public static final int GROUP_EPOCHS_ALL = 0;
	/**
	 * Konstanta zp�sobu pr�ce se skupinou epoch - pr�ce s epochami, jejich�
	 * indexy byly na�ten� ze souboru.
	 */
	public static final int GROUP_EPOCHS_INDEXES = 1;
	/**
	 * Konstanta zp�sobu pr�ce se skupinou epoch - pr�ce se v�emi epochami v
	 * ur�it�m �asov�m intervalu.
	 */
	public static final int GROUP_EPOCHS_TIME = 2;
	/**
	 * Konstanta zp�sobu pr�ce se skupinou epoch - p�id�n� skupiny do pr�m�r�.
	 */
	public static final int GROUP_EPOCHS_ADDING = 0;
	/**
	 * Konstanta zp�sobu pr�ce se skupinou epoch - odebr�n� skupiny z pr�m�r�.
	 */
	public static final int GROUP_EPOCHS_REMOVING = 1;

	/**
	 * Z parametr� zjist�, kolik sn�mk� v z�znamu se vejde do ur�it�ho po�tu
	 * milisekund.
	 * 
	 * @param project
	 *          Projekt, pro kter� se n�vratov� hodnota zji��uje.
	 * @param miliseconds
	 *          Po�et milisekund.
	 * @return Po�et sn�mk�, kter� se vejdou do po�tu milisekund dan�ho atributem
	 *         <code>miliseconds</code>.
	 */
	private static int framesFromMs(SignalProject project, int miliseconds) {
		return (int) ((miliseconds * 1000) / (project.getHeader()
				.getSamplingInterval()));
	}

	/**
	 * Napl�uje vybran� epochy v ur�en�m projektu jejich funk�n�mi hodnotami.
	 * 
	 * @param project
	 *          Projekt, ze kter�ho budou funk�n� hodnoty epoch pln�ny.
	 * @param frames
	 *          Epochy, jejich� funk�n� hodnoty budou napln�ny.
	 * @throws InvalidFrameIndexException
	 */
	private static void fillChannelEpochs(SignalProject project,
			List<ChannelEpoch> frames) throws InvalidFrameIndexException {
		float[] values = null;
		long begin;
		int channelOrderInInputFile;
		int valuesLength = leftEpochBorderInFrames(project)
				+ rightEpochBorderInFrames(project) + 1; // "+1" za st�ed epochy
		for (ChannelEpoch channel : frames) {
			values = new float[valuesLength];

			begin = channel.getFrame() - leftEpochBorderInFrames(project);
			channelOrderInInputFile = channel.getChannelOrderInInputFile();

			for (int i = 0; i < values.length; i++) {
				values[i] = project.getBuffer().getFrame(begin + i)[channelOrderInInputFile];
			}

			channel.setValues(values);
		}
	}

	/**
	 * Z konkr�tn�ho projektu z�sk�v� po�et fram� tvo��c�h lev� okraj epochy.
	 * 
	 * @param project
	 *          Reference na projekt, ze kter�ho bude z�sk�na n�vratov� hodnota.
	 * @return Po�et fram� tvo��c�ch lev� okraj epochy.
	 */
	private static int leftEpochBorderInFrames(SignalProject project) {
		return framesFromMs(project, project.getLeftEpochBorderMs());
	}

	/**
	 * Z konkr�tn�ho projektu z�sk�v� po�et fram� tvo��c�h prav� okraj epochy.
	 * 
	 * @param project
	 *          Reference na projekt, ze kter�ho bude z�sk�na n�vratov� hodnota.
	 * @return Po�et fram� tvo��c�ch prav� okraj epochy.
	 */
	private static int rightEpochBorderInFrames(SignalProject project) {
		return framesFromMs(project, project.getRightEpochBorderMs());
	}

	/**
	 * Napl�uje parametr <code>framesValues</code> funk�n�mi hodnotami fram�,
	 * kter� od nult�ho do posledn�ho indexu tvo�� hodnoty v�sledn�ch pr�m�r�.
	 * Parametr <code>epoch</code> pln� informacemi o v�sledn�ch pr�m�rech.
	 * 
	 * @param project
	 *          Projekt, jeho� v�sledn�mi pr�m�ry budou pln�ny parametry
	 *          <code>framesValues</code> a <code>epoch</code>.
	 * @param framesValues
	 *          Funk�n� hodnoty fram� tvo��c�ch v�sledn� pr�m�ry.
	 * @param epoch
	 *          Inforamce o v�sledn�ch pr�m�rech.
	 * @throws NullPointerException
	 * @throws CorruptedFileException
	 * @throws InvalidFrameIndexException
	 * @throws MathException
	 * @throws IllegalArgumentException
	 */
	public static void calculateAverages(SignalProject project,
			List<float[]> framesValues, Epoch epoch) throws NullPointerException,
			CorruptedFileException, InvalidFrameIndexException,
			IllegalArgumentException, MathException {
		List<Epoch> epochList = project.getAllEpochsList(); // z�sk�n� reference na
																												// seznam v�ech epoch
		/*
		 * Z�sk�n� reference na indexy pr�m�rovan�ch epoch do seznamu "epochList".
		 */
		List<Integer> indexes = project.getAveragedEpochsIndexes();
		int leftEpochBorder = leftEpochBorderInFrames(project);
		int rightEpochBorder = rightEpochBorderInFrames(project);
		int epochLength = leftEpochBorder + rightEpochBorder + 1; // "+1" za st�ed
																															// epochy

		if (indexes.size() > 0) // kdy� je pro pr�m�rov�n� k dispozici alespo� jedna
														// epocha
		{
			try {
				List<float[]> channelsValues = new ArrayList<float[]>(); // seznam
																																	// funk�n�ch
																																	// hodnot
																																	// kan�l� v
																																	// epo�e

				for (int i = 0; i < project.getAveragedSignalsIndexes().size(); i++) // pro
																																							// ka�d�
																																							// kan�l
				{
					List<ChannelEpoch> allEpochs = new ArrayList<ChannelEpoch>(); // seznam
																																				// v�ech
																																				// do
																																				// pr�m�ru
																																				// kan�lu
																																				// zahrnut�ch
																																				// epoch

					int channelOrderInInputFile = project.getAveragedSignalsIndexes()
							.get(i);
					int weight = 0;

					for (int j = 0; j < indexes.size(); j++) // pro ka�dou epochu
					{
						// kdy� je epocha zahrnuta do pr�m�ru tohoto kan�lu
						if (epochList.get(indexes.get(j)).isEpochSelected(
								channelOrderInInputFile)) {
							weight = epochList.get(indexes.get(j)).getWeights()[channelOrderInInputFile];
							// vytvo�en� objektu s informacemi o st�edu epochy, jej� v�ze a
							// po�ad� kan�lu ve vstupn�m souboru
							ChannelEpoch channelEpoch = new ChannelEpoch(
									channelOrderInInputFile, epochList.get(indexes.get(j))
											.getPosition(), weight);
							allEpochs.add(channelEpoch);
						}
					}
					// kdy� je pr�m�r tohot kan�lu tvo�en alespo� jednou epochou
					if (allEpochs.size() > 0) {
						fillChannelEpochs(project, allEpochs); // napln�n� seznamu epoch
																										// jejich funk�n�mi
																										// hodnotami
						epoch.getSelected()[channelOrderInInputFile] = true;
						// v�po�et pr�m�ru a jeho za�azen� do seznamu funk�n�ch hodnot
						// kan�l� v epo�e
						ChannelEpoch channel = Averages.average(allEpochs, project
								.getAverageType());
						channelsValues.add(channel.getValues());
						weight = channel.getWeight();
					} else // pr�m�r neexistuje
					{
						epoch.getSelected()[channelOrderInInputFile] = false;
						channelsValues.add(null); // hodnota pro detekci neexistence pr�m�ru
						weight = 0; // ka�d� existuj�c� pr�m�r m� v�hu alespo� "1"
					}

					epoch.getWeights()[channelOrderInInputFile] = weight;
				}

				/*
				 * AveragingDataManager pracuje s pr�m�ry v�ech kan�l� jako se seznamem
				 * funk�n�ch hodnot pro jednotliv� kan�ly. Metoda m� ale naplnit
				 * parametr "frameValues", ve kter�m jsou pr�m�ry v�ech kan�l� ulo�eny
				 * jako seznam fram�. Tento for-cyklus pouze p�ev�d� prvn� zm�n�nou
				 * reprezentaci pr�m�r� na druhou zm�n�nou reprezentaci pr�m�r�.
				 */
				for (int j = 0; j < epochLength; j++) {
					float[] frameValues = new float[project.getHeader()
							.getNumberOfChannels()];

					for (int i = 0; i < project.getHeader().getNumberOfChannels(); i++) {
						if (channelsValues.get(i) != null) // kdy� pr�m�r existuje
						{
							frameValues[i] = channelsValues.get(i)[j];
						} else {
							frameValues[i] = 0; // kdy� pr�m�r neexistuje, pak bude zobrazen
																	// jako nulov� sign�l
						}
					}

					framesValues.add(frameValues);
				}

				epoch.setType("Mixed");
				epoch.setPosition(leftEpochBorder);
			} catch (NullPointerException e) {
				throw e;
			}
		} else {
			throw new CorruptedFileException("No averaged epochs in project");
		}
	}

	/**
	 * Vytv��� instanci programov�ho rozhran� pro p��stup k atribut�m t��dy
	 * <code>Project</code>.
	 * 
	 * @param project
	 *          Projekt, nad kter�m budou vykon�v�ny po�adovan� akce.
	 */
	public AveragingDataManager(SignalProject project) {
		this.project = project;
	}

	/**
	 * Pro aktu�ln� epochu nastavuje, zda je �i nen� sou��st� pr�m�ru kan�lu
	 * dan�ho parametrem <code>channelOrderInInputFile</code>.
	 * 
	 * @param selected
	 *          <code>true</code>, pokud epocha pat�� do pr�m�ru, jinak
	 *          <code>false</code>.
	 * @param channelOrderInInputFile
	 *          Po�ad� kan�lu ve vstupn�m souboru.
	 */
	public void setEpochInChannelSelected(boolean selected,
			int channelOrderInInputFile) {
		List<Epoch> epochs = project.getAllEpochsList();
		List<Integer> indexes = project.getAveragedEpochsIndexes();
		int index;

		for (int i = 0; i < getEpochWalkingStep(); i++) {
			index = indexes.get(getCurrentEpochNumber() + i);
			epochs.get(index).setEpochSelected(selected, channelOrderInInputFile);
		}
	}

	/**
	 * Nastavuje pro v�echny epochy ve v�ech kan�lech, zda jsou �i nejsou sou��st�
	 * pr�m�r�.
	 * 
	 * @param selected
	 *          <code>true</code>, pokud epochy pat�� do pr�m�r�, jinak
	 *          <code>false</code>.
	 */
	public void setAllEpochsSelected(boolean selected) {
		for (int i = 0; i < project.getHeader().getNumberOfChannels(); i++) {
			if (project.getApplyChanges()[i]) {
				setAllEpochsInChannelSelected(selected, i);
			}
		}
	}

	/**
	 * Nastavuje pro v�echny epochy v konkr�tn�m kan�lu, zda jsou �i nejsou
	 * sou��st� pr�m�ru.
	 * 
	 * @param selected
	 *          <code>true</code>, pokud epochy pat�� do pr�m�ru, jinak
	 *          <code>false</code>.
	 * @param channelOrderInInputFile
	 *          Po�ad� kan�lu ve vstupn�m souboru.
	 */
	public void setAllEpochsInChannelSelected(boolean selected,
			int channelOrderInInputFile) {
		List<Epoch> epochs = project.getAllEpochsList();
		List<Integer> indexes = project.getAveragedEpochsIndexes();
		int index;

		for (int i = 0; i < indexes.size(); i++) {
			index = indexes.get(i);
			epochs.get(index).setEpochSelected(selected, channelOrderInInputFile);
		}
	}

	/**
	 * Nastavuje pro v�echny epochy v ur�it�m �asov�m intervalu ve v�ech kan�lech,
	 * zda jsou �i nejsou sou��st� pr�m�r�.
	 * 
	 * @param selected
	 *          <code>true</code>, pokud epochy pat�� do pr�m�r�, jinak
	 *          <code>false</code>.
	 */
	public void setTimeEpochsSelected(boolean selected) {
		List<Integer> epochsIndexes = new ArrayList<Integer>();
		int leftBorder = getFramesFromMs(getTimeSelectionBegin());
		int rightBorder = getFramesFromMs(getTimeSelectionEnd());

		List<Epoch> epochList = project.getAllEpochsList();
		List<Integer> averagedIndexes = project.getAveragedEpochsIndexes();

		for (int i = 0; i < averagedIndexes.size(); i++) {
			long position = epochList.get(averagedIndexes.get(i)).getPosition();

			if (leftBorder <= (position - getLeftEpochBorderInFrames())
					&& (position + getRightEpochBorderInFrames()) <= rightBorder) {
				epochsIndexes.add(new Integer(i));
			}
			/*
			 * prohled�v�n� od za��tku se nelze vyhnout, je ale zbyte�n� testovat i ty
			 * epochy, kter� le�� prokazateln� za koncem �asov�ho intervalu
			 */
			if ((position + getRightEpochBorderInFrames()) > rightBorder) {
				break;
			}
		}

		for (int i = 0; i < project.getHeader().getNumberOfChannels(); i++) {
			if (project.getApplyChanges()[i]) {
				setIndexedEpochsInChannelSelected(selected, i, epochsIndexes);
			}
		}
	}

	/**
	 * Nastavuje pro v�echny epochy, jejich� indexy byly z�sk�ny ze souboru, ve
	 * v�ech kan�lech, zda jsou �i nejsou sou��st� pr�m�r�.
	 * 
	 * @param selected
	 *          <code>true</code>, pokud epochy pat�� do pr�m�r�, jinak
	 *          <code>false</code>.
	 */
	public void setIndexedEpochsSelected(boolean selected) {
		List<Integer> indexes = project.getLoadedEpochsIndexes();

		for (int i = 0; i < project.getHeader().getNumberOfChannels(); i++) {
			if (project.getApplyChanges()[i]) {
				setIndexedEpochsInChannelSelected(selected, i, indexes);
			}
		}
	}

	/**
	 * Nastavuje pro v�echny epochy, jejich� indexy jsou p�ed�ny jako atribut, pro
	 * konkr�nt� kan�l, zda jsou �i nejsou sou��st� pr�m�ru.
	 * 
	 * @param selected
	 *          <code>true</code>, pokud epochy pat�� do pr�m�r�, jinak
	 *          <code>false</code>.
	 * @param channelOrderInInputFile
	 *          Po�ad� kan�lu ve vstupn�m souboru.
	 * @param epochsIndexes
	 *          Indexy epoch, kter� jsou za�azov�ny/odeb�r�ny do/z pr�m�ru.
	 */
	public void setIndexedEpochsInChannelSelected(boolean selected,
			int channelOrderInInputFile, List<Integer> epochsIndexes) {
		List<Epoch> epochs = project.getAllEpochsList();
		List<Integer> indexes = project.getAveragedEpochsIndexes();

		for (int i = 0; i < indexes.size(); i++) {
			if (epochsIndexes.contains(indexes.get(i))) {
				epochs.get(indexes.get(i)).setEpochSelected(selected,
						channelOrderInInputFile);
			}
		}
	}

	/**
	 * Slou�� pro zobrazen� n�sleduj�c� epochy.
	 * 
	 * @return Data pro zobrazen� v prezenta�n� vrstv�.
	 * @throws InsufficientDataException
	 * @throws InvalidFrameIndexException
	 */
	public List<EpochDataSet> nextEpoch() throws InsufficientDataException,
			InvalidFrameIndexException {
		setCurrentEpochNumber(getCurrentEpochNumber() + getEpochWalkingStep());
		return getEpochDataSet();
	}

	/**
	 * Slou�� pro zobrazen� p�edchoz� epochy.
	 * 
	 * @return Data pro zobrazen� v prezenta�n� vrstv�.
	 * @throws InsufficientDataException
	 * @throws InvalidFrameIndexException
	 */
	public List<EpochDataSet> previousEpoch() throws InsufficientDataException,
			InvalidFrameIndexException {
		setCurrentEpochNumber(getCurrentEpochNumber() - getEpochWalkingStep());
		return getEpochDataSet();
	}

	/**
	 * Slou�� pro zobrazen� epochy, jej� ��slo je p�ed�no jako parametr.
	 * 
	 * @param epochNumber
	 *          ��slo epochy, kter� se m� zobrazit.
	 * @return Data pro zobrazen� v prezenta�n� vrstv�.
	 * @throws InsufficientDataException
	 * @throws InvalidFrameIndexException
	 */
	public List<EpochDataSet> jumpToEpoch(int epochNumber)
			throws InsufficientDataException, InvalidFrameIndexException {
		setCurrentEpochNumber(epochNumber);
		return getEpochDataSet();
	}

	/**
	 * Vol� metody pro nahr�n� index� epoch ze souboru, kter� je p�ed�n jako
	 * parametr.
	 * 
	 * @param file
	 *          Soubor s indexy epoch.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void loadEpochsIndexes(File file) throws FileNotFoundException,
			IOException {
		IndexesLoader loader = new IndexesLoader();
		project.setLoadedEpochsIndexes(loader.loadIndexes(file));
		project.setAbsolutePathToIndexes(file.getAbsolutePath());
	}

	/**
	 * Vrac� data pro zobrazen� v prezenta�n� vrstv� - pro pohled
	 * <code>GroupEpochsView</code>.
	 * 
	 * @return Data pro zobrazen� v <code>GroupEpochsView</code>.
	 * @throws InsufficientDataException
	 * @throws InvalidFrameIndexException
	 */
	public List<EpochDataSet> getGroupEpochsDataSet()
			throws InsufficientDataException, InvalidFrameIndexException {
		List<EpochDataSet> dataSet = new ArrayList<EpochDataSet>();

		switch (project.getLastUsedGroupEpochsType()) {
		case GROUP_EPOCHS_TIME:
			fillTimeEpochsDataSet(dataSet);
			break;
		case GROUP_EPOCHS_INDEXES:
			fillIndexedEpochsDataSet(dataSet);
			break;
		default:
			fillAllEpochsDataSet(dataSet);
		}

		return dataSet;
	}

	/**
	 * Slou�� k napln�n� dataSetu pro zobrazen� v pohledu
	 * <code>GroupEpochsView</code> epochami vyskytuj�c�mi se v ur�it�m �asov�m
	 * intervalu.
	 * 
	 * @param dataSet
	 *          Data k napln�n�.
	 * @throws InsufficientDataException
	 */
	private void fillTimeEpochsDataSet(List<EpochDataSet> dataSet)
			throws InsufficientDataException {
		List<Integer> epochsIndexes = new ArrayList<Integer>();
		int leftBorder = getFramesFromMs(getTimeSelectionBegin());
		int rightBorder = getFramesFromMs(getTimeSelectionEnd());

		List<Epoch> epochList = project.getAllEpochsList();
		List<Integer> averagedIndexes = project.getAveragedEpochsIndexes();

		for (int i = 0; i < averagedIndexes.size(); i++) {
			long position = epochList.get(averagedIndexes.get(i)).getPosition();

			if (leftBorder <= position && position <= rightBorder) {
				epochsIndexes.add(new Integer(i));
			}
		}

		fillDataSetByIndexes(dataSet, epochsIndexes);

	}

	/**
	 * Slou�� k napln�n� dataSetu pro zobrazen� v pohledu
	 * <code>GroupEpochsView</code> epochami jejich� indexy byly na�teny ze
	 * souboru.
	 * 
	 * @param dataSet
	 *          Data k napln�n�.
	 * @throws InsufficientDataException
	 */
	private void fillIndexedEpochsDataSet(List<EpochDataSet> dataSet)
			throws InsufficientDataException {
		List<Integer> epochsIndexes = project.getLoadedEpochsIndexes();
		fillDataSetByIndexes(dataSet, epochsIndexes);
	}

	/**
	 * Slou�� k napln�n� dataSetu pro zobrazen� v pohledu
	 * <code>GroupEpochsView</code> v�emi epochami.
	 * 
	 * @param dataSet
	 *          Data k napln�n�.
	 * @throws InsufficientDataException
	 */
	private void fillAllEpochsDataSet(List<EpochDataSet> dataSet)
			throws InsufficientDataException, InvalidFrameIndexException {
		List<Integer> epochsIndexes = new ArrayList<Integer>();

		for (int i = 0; i < project.getAveragedEpochsIndexes().size(); i++) {
			epochsIndexes.add(new Integer(i));
		}

		fillDataSetByIndexes(dataSet, epochsIndexes);
	}

	/**
	 * Slou�� k napln�n� dataSetu pro zobrazen� v pohledu
	 * <code>GroupEpochsView</code> epochami jejich� indexy jsou p�ed�ny v
	 * parametru <code>epochsIndexes</code>.
	 * 
	 * @param dataSet
	 *          Data k napln�n�.
	 * @param epochsIndexes
	 *          Indexy epoch.
	 * @throws InsufficientDataException
	 */
	private void fillDataSetByIndexes(List<EpochDataSet> dataSet,
			List<Integer> epochsIndexes) throws InsufficientDataException {
		if (epochsIndexes != null) {
			try {
				List<Epoch> epochList = project.getAllEpochsList();
				List<Integer> averagedIndexes = project.getAveragedEpochsIndexes();

				for (int i = 0; i < project.getAveragedSignalsIndexes().size(); i++) {
					List<ChannelEpoch> currentEpoch = new ArrayList<ChannelEpoch>();
					List<ChannelEpoch> currentEpochWithAvg = new ArrayList<ChannelEpoch>();
					List<ChannelEpoch> currentEpochWithoutAvg = new ArrayList<ChannelEpoch>();
					List<ChannelEpoch> currentAvg = new ArrayList<ChannelEpoch>();
					boolean checked = true;
					int channelOrderInInputFile = 0;
					int weight;

					for (int j = 0; j < averagedIndexes.size(); j++) {
						channelOrderInInputFile = project.getAveragedSignalsIndexes()
								.get(i);
						weight = epochList.get(averagedIndexes.get(j)).getWeights()[channelOrderInInputFile];

						ChannelEpoch channelEpoch = new ChannelEpoch(
								channelOrderInInputFile, epochList.get(averagedIndexes.get(j))
										.getPosition(), weight);

						if (epochsIndexes.contains(new Integer(j))) {
							currentEpoch.add(channelEpoch);
							if (!(epochList.get(averagedIndexes.get(j)).getSelected()[i])) {
								checked = false;
							}
						}

						if (epochList.get(averagedIndexes.get(j)).isEpochSelected(
								channelOrderInInputFile)) {
							currentAvg.add(channelEpoch);

							if (!epochsIndexes.contains(new Integer(j))) {
								currentEpochWithoutAvg.add(channelEpoch);
							}
						}

						if (epochsIndexes.contains(new Integer(j))
								|| epochList.get(averagedIndexes.get(j)).isEpochSelected(
										channelOrderInInputFile)) {
							currentEpochWithAvg.add(channelEpoch);
						}

					}

					if (currentEpoch.size() == 0) {
						checked = false;
					}

					dataSet
							.add(createEpochDataSet(channelOrderInInputFile, currentEpoch,
									currentEpochWithAvg, currentEpochWithoutAvg, currentAvg,
									checked));
				}
			} catch (Exception exception) {
				exception.printStackTrace();
				throw new InsufficientDataException("Insufficient data for epoch view.");
			}
		}
	}

	/**
	 * Vrac� data pro zobrazen� aktu�ln� epochy v prezenta�n� vrstv� - pro pohledy
	 * <code>EpochByEpochView</code> a <code>AveragesPreView</code>.
	 * 
	 * @return Data pro zobrazen� v pohledech <code>EpochByEpochView</code> a
	 *         <code>AveragesPreView</code>..
	 * @throws InsufficientDataException
	 * @throws InvalidFrameIndexException
	 */
	public List<EpochDataSet> getEpochDataSet() throws InsufficientDataException,
			InvalidFrameIndexException {
		return getEpochDataSet(getCurrentEpochNumber());
	}

	/**
	 * Vrac� data pro zobrazen� epochy, jej� index je p�ed�n v parametru
	 * <code>epochNumber</code>, v prezenta�n� vrstv� - pro pohledy
	 * <code>EpochByEpochView</code> a <code>AveragesPreView</code>.
	 * 
	 * @param epochNumber
	 *          Index epochy, jej� data se maj� zobrazit.
	 * @return Data pro zobrazen� v pohledech <code>EpochByEpochView</code> a
	 *         <code>AveragesPreView</code>..
	 * @throws InsufficientDataException
	 * @throws InvalidFrameIndexException
	 */
	public List<EpochDataSet> getEpochDataSet(int epochNumber)
			throws InsufficientDataException, InvalidFrameIndexException {
		List<EpochDataSet> currentEpochDataSet = new ArrayList<EpochDataSet>();

		final int firstEpoch = epochNumber;
		/*
		 * D�lka epochy je v�dy o jedni�ku men��, ne� po�et epoch, o kter� se
		 * posunuje p�i vyvol�n� metody "nextEpoch", proto ta "-1" na konci
		 * n�sleduj�c�ho ��dku.
		 */
		final int lastEpoch = epochNumber + getEpochWalkingStep() - 1;

		try {
			List<Epoch> epochList = project.getAllEpochsList();
			List<Integer> indexes = project.getAveragedEpochsIndexes();

			if (indexes.size() > 0) {
				for (int i = 0; i < project.getAveragedSignalsIndexes().size(); i++) {
					List<ChannelEpoch> currentEpoch = new ArrayList<ChannelEpoch>();
					List<ChannelEpoch> currentEpochWithAvg = new ArrayList<ChannelEpoch>();
					List<ChannelEpoch> currentEpochWithoutAvg = new ArrayList<ChannelEpoch>();
					List<ChannelEpoch> currentAvg = new ArrayList<ChannelEpoch>();
					boolean checked = true;

					int channelOrderInInputFile = project.getAveragedSignalsIndexes()
							.get(i);

					for (int j = firstEpoch; j <= lastEpoch; j++) {

						int weight = epochList.get(indexes.get(j)).getWeights()[channelOrderInInputFile];
						ChannelEpoch channelEpoch = new ChannelEpoch(
								channelOrderInInputFile, epochList.get(indexes.get(j))
										.getPosition(), weight);
						currentEpoch.add(channelEpoch);
						currentEpochWithAvg.add(channelEpoch);

						if (!(epochList.get(indexes.get(j))
								.isEpochSelected(channelOrderInInputFile))) {
							checked = false;
						} else {
							currentAvg.add(channelEpoch);
						}
					}

					for (int j = 0; j < indexes.size(); j++) {
						if (j < firstEpoch || j > lastEpoch) {
							if (epochList.get(indexes.get(j)).isEpochSelected(
									channelOrderInInputFile)) {
								int weight = epochList.get(indexes.get(j)).getWeights()[channelOrderInInputFile];
								ChannelEpoch channelEpoch = new ChannelEpoch(
										channelOrderInInputFile, epochList.get(indexes.get(j))
												.getPosition(), weight);
								currentEpochWithoutAvg.add(channelEpoch);
								currentEpochWithAvg.add(channelEpoch);
								currentAvg.add(channelEpoch);
							}
						}
					}

					currentEpochDataSet.add(createEpochDataSet(channelOrderInInputFile,
							currentEpoch, currentEpochWithAvg, currentEpochWithoutAvg,
							currentAvg, checked));
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new InsufficientDataException("Insufficient data for epoch view.");
		}

		return currentEpochDataSet;
	}

	/**
	 * Vytv��� objekt nesouc� informace o jedn� epo�e v jednom kan�lu pro
	 * zobrazen� v prezenta�n� vrstv�.
	 * 
	 * @param channelOrderInInputFile
	 *          Po�ad� kan�lu ve vstupn�m souboru.
	 * @param currentEpoch
	 *          Funk�n� hodnoty pr�v� zobrazovan� epochy.
	 * @param currentEpochWithAvg
	 *          Funk�n� hodnoty pr�m�ru po zahrnut� pr�v� zobrazovan� epochy.
	 * @param currentEpochWithoutAvg
	 *          Funk�n� hodnoty pr�m�ru bet zahrnut� pr�v� zobrazovan� epochy.
	 * @param currentAvg
	 *          Funk�n� hodnoty aktu�ln�ho pr�m�ru.
	 * @param checked
	 *          ��k�, zda je pr�v� zobrazovan� epocha zahrnut� do aktu�ln�ho
	 *          pr�m�ru.
	 * @return Objekt nesouc� informace o jedn� epo�e v jednom kan�lu
	 * @throws InvalidFrameIndexException
	 * @throws IllegalArgumentException
	 * @throws MathException
	 */
	private EpochDataSet createEpochDataSet(int channelOrderInInputFile,
			List<ChannelEpoch> currentEpoch, List<ChannelEpoch> currentEpochWithAvg,
			List<ChannelEpoch> currentEpochWithoutAvg, List<ChannelEpoch> currentAvg,
			boolean checked) throws InvalidFrameIndexException,
			IllegalArgumentException, MathException {
		fillWithFunctionValues(currentEpoch);
		fillWithFunctionValues(currentEpochWithAvg);
		fillWithFunctionValues(currentEpochWithoutAvg);
		fillWithFunctionValues(currentAvg);

		ChannelEpoch currentEpochValues = Averages.average(currentEpoch, project
				.getAverageType());
		ChannelEpoch currentEpochWithAvgValues = Averages.average(
				currentEpochWithAvg, project.getAverageType());
		ChannelEpoch currentEpochWithoutAvgValues = Averages.average(
				currentEpochWithoutAvg, project.getAverageType());
		ChannelEpoch currentAvgValues = Averages.average(currentAvg, project
				.getAverageType());

		return new EpochDataSet(channelOrderInInputFile, checked, currentAvgValues
				.getTrustful(), currentEpochValues.getValues(),
				currentEpochWithAvgValues.getValues(), currentEpochWithoutAvgValues
						.getValues(), currentAvgValues.getValues());
	}

	/**
	 * Pln� objekty p�edan� v kolekci v atributu <code>frames</code> funk�n�mi
	 * hodnotami aktu�ln�ho projektu.
	 * 
	 * @param frames
	 *          Objekty k napln�n� funk�n�mi hodnotami.
	 * @throws InvalidFrameIndexException
	 */
	private void fillWithFunctionValues(List<ChannelEpoch> frames)
			throws InvalidFrameIndexException {
		float[] values = null;
		long begin;
		int channelOrderInInputFile;

		for (ChannelEpoch channel : frames) {
			values = new float[getLeftEpochBorderInFrames()
					+ getRightEpochBorderInFrames() + 1]; // "+1" za st�ed epochy

			begin = channel.getFrame() - getLeftEpochBorderInFrames();
			channelOrderInInputFile = channel.getChannelOrderInInputFile();

			for (int i = 0; i < values.length; i++) {
				values[i] = project.getBuffer().getFrame(begin + i)[channelOrderInInputFile];
			}

			channel.setValues(values);
		}
	}

	/**
	 * Vrac� hodnotu atributu <code>Project.leftEpochBorderMS</code> p�epo�tenou
	 * na framy.
	 * 
	 * @return Po�et fram� tvo��c�ch lev� okraj epochy.
	 */
	public int getLeftEpochBorderInFrames() {
		return getFramesFromMs(project.getLeftEpochBorderMs());
	}

	/**
	 * Vrac� hodnotu atributu <code>Project.rightEpochBorderMS</code>
	 * p�epo�tenou na framy.
	 * 
	 * @return Po�et fram� tvo��c�ch prav� okraj epochy.
	 */
	public int getRightEpochBorderInFrames() {
		return getFramesFromMs(project.getRightEpochBorderMs());
	}

	/**
	 * P�epo��t�v� milisekundy na po�et fram�.
	 * 
	 * @param miliseconds
	 *          Po�et milisekund.
	 * @return Po�et fram�.
	 */
	private int getFramesFromMs(int miliseconds) {
		return (int) ((miliseconds * 1000) / (project.getHeader()
				.getSamplingInterval()));
	}

	/**
	 * Nastavuje hodnotu atributu <code>Project.averageType</code>.
	 * 
	 * @param averageType
	 *          Typ pr�m�ru, kter� se m� pou��t pro v�po�et pr�m�ru epoch.
	 */
	public void setAverageType(int averageType) {
		if (project != null)
			project.setAverageType(averageType);
	}

	/**
	 * Vrac� hodnotu atributu <code>Project.averageType</code>.
	 * 
	 * @return Typ pr�m�ru, kter� se pou��v� pro v�po�et pr�m�ru epoch.
	 */
	public int getAverageType() {
		if (project != null)
			return project.getAverageType();
		else
			return 0;
	}

	/**
	 * Vrac� referenci na atribut <code>Project.buffer</code>.
	 * 
	 * @return Reference na objekt, kter� je abstrakc� p��stupu k otev�en�mu
	 *         datov�mu souboru.
	 */
	public Buffer getBuffer() {
		return project.getBuffer();
	}

	/**
	 * Vrac� hodnotu atributu <code>Project.currentEpochNumber</code>.
	 * 
	 * @return ��slo pr�v� zobrazovan� epochy.
	 */
	public int getCurrentEpochNumber() {
		return project.getCurrentEpochNumber();
	}

	/**
	 * Nastavuje hodnotu atributu <code>Project.currentEpochNumber</code>.
	 * 
	 * @param currentEpochNumber
	 *          currentEpochNumber.
	 */
	public void setCurrentEpochNumber(int currentEpochNumber) {
		if (currentEpochNumber < project.getAveragedEpochsIndexes().size()) {
			project.setCurrentEpochNumber(currentEpochNumber);
		} else if (project.getAveragedEpochsIndexes().size() > 0) {
			project
					.setCurrentEpochNumber(project.getAveragedEpochsIndexes().size() - 1);
		} else {
			project.setCurrentEpochNumber(0);
		}
	}

	/**
	 * Vrac� referenci na atribut <code>Project.averagedSignalsIndexes</code>.
	 * 
	 * @return Seznam index� kan�l�, kter� se pr�m�ruj�.
	 */
	public List<Integer> getAveragedSignalsIndexes() {
		return project.getAveragedSignalsIndexes();
	}

	/**
	 * Vrac� referenci na atribut <code>Project.averagedEpochsIndexes</code>.
	 * 
	 * @return Seznam index� epoch, kter� se pr�m�ruj�.
	 */
	public List<Integer> getAveragedEpochsIndexes() {
		return project.getAveragedEpochsIndexes();
	}

	/**
	 * Vrac� referenci na atribut <code>Project.header</code>.
	 * 
	 * @return Objekt s hlavi�kov�mi informacemi o vstupn�m souboru.
	 */
	public Header getHeader() {
		return project.getHeader();
	}

	/**
	 * Nastavuje projekt, se kter�m u�ivatel pr�v� pracuje.
	 * 
	 * @param project
	 *          reference na projekt, se kter�m u�ivatel pr�v� pracuje.
	 */
	public void setProject(SignalProject project) {
		this.project = project;
	}

	/**
	 * Nastavuje hodnotu atributu <code>Project.leftEpochBorderMs</code>.
	 * 
	 * @param leftEpochBorder
	 *          Velikost lev�ho okraje epochy v milisekund�ch.
	 */
	public void setLeftEpochBorder(int leftEpochBorder) {
		project.setLeftEpochBorderMs(leftEpochBorder);
	}

	/**
	 * Nastavuje hodnotu atributu <code>Project.rightEpochBorderMs</code>.
	 * 
	 * @param rightEpochBorder
	 *          Velikost prav�ho okraje epochy v milisekund�ch.
	 */
	public void setRightEpochBorder(int rightEpochBorder) {
		project.setRightEpochBorderMs(rightEpochBorder);
	}

	/**
	 * Nastavuje velikost n-tice epoch.
	 * 
	 * @param step
	 *          Po�et epoch v n-tici.
	 */
	public void setEpochWalkingStep(int step) {
		project.setEpochWalkingStep(step);
	}

	/**
	 * Vrac� velikost n-tice epoch.
	 * 
	 * @return Po�et epoch v n-tici.
	 */
	public int getEpochWalkingStep() {
		return project.getEpochWalkingStep();
	}

	/**
	 * Nastavuje, kter� kan�ly se ��astn� hromadn�ho za�azov�n�/odeb�r�n� epoch
	 * do/z pr�m�r�.
	 * 
	 * @param applyChanges
	 *          Kdy� se kan�l ��astn�, tak <code>true</code>, jinak
	 *          <code>false</code>.
	 * @param channelOrderInInputFile
	 *          Po�ad� kan�lu ve vstupn�m souboru.
	 */
	public void setApplyChanges(boolean applyChanges, int channelOrderInInputFile) {
		project.getApplyChanges()[channelOrderInInputFile] = applyChanges;
	}

	/**
	 * Vrac�, zda se kan�l ��astn� hromadn�ho za�azov�n�/odeb�r�n� epoch do/z
	 * pr�m�ru.
	 * 
	 * @param channelOrderInInputFile
	 *          Po�ad� kan�lu ve vstupn�m souboru.
	 * @return <code>true</code>, pokud se kan�l ��astn�, jinak
	 *         <code>false</code>.
	 */
	public boolean getApplyChanges(int channelOrderInInputFile) {
		return project.getApplyChanges()[channelOrderInInputFile];
	}

	/**
	 * Nastavuje indexy epoch, kter� byly nahr�ny ze souboru.
	 * 
	 * @param loadedEpochsIndexes
	 *          Indexy epoch.
	 */
	public void setLoadedEpochsIndexes(List<Integer> loadedEpochsIndexes) {
		project.setLoadedEpochsIndexes(loadedEpochsIndexes);
	}

	/**
	 * Vrac� posledn� pou�it� typ hromadn�ho za�azov�n�/odeb�r�n� epoch do/z
	 * pr�m�r�.
	 * 
	 * @return the lastUsedGroupEpochsType typ hromadn�ho za�azov�n�/odeb�r�n�
	 *         (konstanty t��dy <code>AveragingDataManager</code> za��naj�c�
	 *         <code>GROUP_EPOCHS_</code>).
	 */
	public int getLastUsedGroupEpochsType() {
		return project.getLastUsedGroupEpochsType();
	}

	/**
	 * Nastavuje posledn� pou�it� typ hromadn�ho za�azov�n�/odeb�r�n� epoch do/z
	 * pr�m�r�.
	 * 
	 * @param lastUsedGroupEpochsType
	 *          typ hromadn�ho za�azov�n�/odeb�r�n� (konstanty t��dy
	 *          <code>AveragingDataManager</code> za��naj�c�
	 *          <code>GROUP_EPOCHS_</code>).
	 */
	public void setLastUsedGroupEpochsType(int lastUsedGroupEpochsType) {
		project.setLastUsedGroupEpochsType(lastUsedGroupEpochsType);
	}

	/**
	 * Vrac� absolutn� cestu k souboru, ze kter�ho byly naposled na�teny indexy
	 * epcoh.
	 * 
	 * @return Absolutn� cesta k souboru.
	 */
	public String getAbsolutePathToIndexes() {
		return project.getAbsolutePathToIndexes();
	}

	/**
	 * Vrac� zp�sob pr�ce se skupinou epoch v pr�m�rovac� komponent�.
	 * 
	 * @return n�kter� z konstant t��dy <code>AveragingDataManager</code>.
	 */
	public int getGroupEpochWorkMode() {
		return project.getGroupEpochWorkMode();
	}

	/**
	 * Nastavuje zp�sob pr�ce se skupinou epoch v pr�m�rovac� komponent�.
	 * 
	 * @param groupEpochWorkMode
	 *          n�kter� z konstant t��dy <code>AveragingDataManager</code>.
	 */
	public void setGroupEpochWorkMode(int groupEpochWorkMode) {
		project.setGroupEpochWorkMode(groupEpochWorkMode);
	}

	/**
	 * Milisekunda definuj�c� po��tek �asov�ho intervalu v n�m� obsa�en� epochy
	 * jsou ur�eny pro za�azov�n�/odeb�r�n� do/z pr�m�r�.
	 * 
	 * @return milisekunda za��naj�c� �asov� interval
	 */
	public int getTimeSelectionBegin() {
		return project.getTimeSelectionBegin();
	}

	/**
	 * Milisekunda definuj�c� po��tek �asov�ho intervalu v n�m� obsa�en� epochy
	 * jsou ur�eny pro za�azov�n�/odeb�r�n� do/z pr�m�r�.
	 * 
	 * @param timeSelectionBegin
	 *          milisekunda za��naj�c� �asov� interval
	 */
	public void setTimeSelectionBegin(int timeSelectionBegin) {
		project.setTimeSelectionBegin(timeSelectionBegin);
	}

	/**
	 * Vrac� milisekundu definuj�c� konec �asov�ho intervalu v n�m� obsa�en�
	 * epochy jsou ur�eny pro za�azov�n�/odeb�r�n� do/z pr�m�r�.
	 * 
	 * @return milisekunda ukon�uj�c� �asov� interval
	 */
	public int getTimeSelectionEnd() {
		return project.getTimeSelectionEnd();
	}

	/**
	 * Nastavuje milisekundu definuj�c� konec �asov�ho intervalu v n�m� obsa�en�
	 * epochy jsou ur�eny pro za�azov�n�/odeb�r�n� do/z pr�m�r�.
	 * 
	 * @param timeSelectionEnd
	 *          milisekunda ukon�uj�c� �asov� interval
	 */
	public void setTimeSelectionEnd(int timeSelectionEnd) {
		project.setTimeSelectionEnd(timeSelectionEnd);
	}
}
