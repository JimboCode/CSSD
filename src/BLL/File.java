package BLL;

/**
 * Store the file location and maintains a version where changed
 * @author James Staite
 */
class File
{
    // filename including path
    private String name;

    // status of the file e.g. DEFECTIVE, OBSOLETE etc.
    private FileStatus status;

    // current version
    private int version;

    // associated QC report with this file
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
     * Provides the filename
     * @return the name of the file including it path
     */
    public String getName() {
        return name;
    }

    /**
     * Provides the status of the file
     * @return the status of the file
     */
    public FileStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of the file
     * @param status status to be applied to the file
     */
    public void setStatus(FileStatus status)
    {
        this.status = status;
    }

    /**
     * Provides the version of the file
     * @return the version the file version
     */
    public int getVersion() {
        return version;
    }

    /**
     * Set the QC report associated with this file
     * @param report The report object to be associated
     */
    public void setQCReport(QCReport report)
    {
        this.report = report;
    }

    /**
     * Provides the the QC Report associated with this file if there is one
     * @return Null or a QC Report
     */
    public QCReport getQCReport()
    {
        return report;
    }
}    
