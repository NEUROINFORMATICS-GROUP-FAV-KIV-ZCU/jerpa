package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import org.junit.BeforeClass;
import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import ch.ethz.origo.jerpa.prezentation.perspective.EDEDBPerspective;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Petr Miko
 */
public class ActionAnalyseSelectedTest {

    private static Method printExtensionsMethod;
    private static Method isAnalysibleMethod;
    private static EDEDBPerspective perspective;
    private static EDEDClient session;
    private static EDEDBController controller;
    private static ActionAnalyseSelected actionClass;
    
    /**
     * Test class initialization.
     * @throws NoSuchMethodException 
     */
    @BeforeClass
    public static void setupClass() throws Exception {
        perspective = new EDEDBPerspective();
        session = new EDEDClient();
        controller = new EDEDBController(perspective, session);
        
        actionClass = new ActionAnalyseSelected(controller);
        
        printExtensionsMethod = ActionAnalyseSelected.class.getDeclaredMethod("printExtensions");
        printExtensionsMethod.setAccessible(true);
        
        isAnalysibleMethod = ActionAnalyseSelected.class.getDeclaredMethod("isAnalysable",String.class);
        isAnalysibleMethod.setAccessible(true);
        
        System.out.print("* EDEDB - ActionAnalyseSelected test");
        
    }
    
    /**
     * Testing private method printExtensions.
     * 
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException 
     */
    @Test
    public void printExtensions() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        System.out.print("- extensions test");
        String extOutput = (String) printExtensionsMethod.invoke(actionClass);
        System.out.println("("+ extOutput+")");
        String[] extensions = extOutput.split(", ");
        assertTrue (extensions.length > 0);
    }
    
    /**
     * Testing private method of analysability of extension.
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException 
     */
    @Test
    public void isAnalysable() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        System.out.println("- analysibility test");
        assertFalse((Boolean) isAnalysibleMethod.invoke(actionClass,"nonsense"));
        assertTrue((Boolean) isAnalysibleMethod.invoke(actionClass, "vhdr"));
    }

}
