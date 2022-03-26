package engine.dto;

public class DtoCompilationTask {
    private String absolutePathToCompile;
    private String absolutePathForCompiled;

    public DtoCompilationTask() {    }
    public DtoCompilationTask(String absolutePathToCompile, String absolutePathForCompiled) {
        this.absolutePathToCompile=absolutePathToCompile;
        this.absolutePathForCompiled=absolutePathForCompiled;
    }

    public String getAbsolutePathToCompile() {
        return absolutePathToCompile;
    }

    public void setAbsolutePathToCompile(String absolutePathToCompile) {
        this.absolutePathToCompile = absolutePathToCompile;
    }

    public String getAbsolutePathForCompiled() {
        return absolutePathForCompiled;
    }

    public void setAbsolutePathForCompiled(String absolutePathForCompiled) {
        this.absolutePathForCompiled = absolutePathForCompiled;
    }


}
