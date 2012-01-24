package ch.ethz.origo.jerpa.data.tier.pojo;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 14.12.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class FileMetadataParamDef {
    private String paramName;

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    private int fileMetadataParamDefId;

    public int getFileMetadataParamDefId() {
        return fileMetadataParamDefId;
    }

    public void setFileMetadataParamDefId(int fileMetadataParamDefId) {
        this.fileMetadataParamDefId = fileMetadataParamDefId;
    }

    private String paramDataType;

    public String getParamDataType() {
        return paramDataType;
    }

    public void setParamDataType(String paramDataType) {
        this.paramDataType = paramDataType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileMetadataParamDef that = (FileMetadataParamDef) o;

        if (fileMetadataParamDefId != that.fileMetadataParamDefId) return false;
        if (paramDataType != null ? !paramDataType.equals(that.paramDataType) : that.paramDataType != null)
            return false;
        if (paramName != null ? !paramName.equals(that.paramName) : that.paramName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = paramName != null ? paramName.hashCode() : 0;
        result = 31 * result + fileMetadataParamDefId;
        result = 31 * result + (paramDataType != null ? paramDataType.hashCode() : 0);
        return result;
    }
}
