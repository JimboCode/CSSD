/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL;

/**
 *
 * @author James Staite
 */
class File
{
    private String name;

    private FileStatus status;

    private int version;

    private QCReport report;

    /**
     * Details of file component stored in a MediaItem
     * @param name Filename
     * @param status file status of type FileStatus enumeration
     * @param version integer of the version number
     */
    File(String name, FileStatus status, int version)
    {
        this.name = name;
        this.status = status;
        this.version = version;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the status
     */
    public FileStatus getStatus() {
        return status;
    }

    public void setStatus(FileStatus status)
    {
        this.status = status;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    public void setQCReport(QCReport report)
    {
        this.report = report;
    }

    public QCReport getQCReport()
    {
        return report;
    }
}    
