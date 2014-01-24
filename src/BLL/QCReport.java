package BLL;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

/**
 * QC Report
 * 
 * @author James Staite
 */
public class QCReport 
{
    // a list of fault if any in the report
    private EventList<Fault> faultsList = GlazedLists.threadSafeList(new BasicEventList<Fault>());
    
    // if the report has been moderated
    private boolean reportmoderated = false;
    
    // file used in this report
    private File file;
    
    /**
     * Creates the report and store details of the file it is about
     * @param file 
     */
    public QCReport(File file)
    {
        this.file = file;
    }
    
    /**
     * provides the EventList for use in the UI Jtables
     * @return EventList<Fault>
     */
    public EventList<Fault> getReportList()
    {
        return faultsList;
    }

    /**
     * Adds a fault to the QC Report
     * @param description Description of the fault
     * @param position position of the fault e.g. 1:00
     * @param severity the severity of the fault
     */
    public void addFault(String description, String position, int severity)
    {
        // create new fault item
        Fault newfault = new Fault(description, position, severity);
        
        // add to list
        faultsList.add(newfault);
    }
    
    /**
     * removes a fault from the list
     * @param fault the item to be removed
     */
    public void removeFault(Fault fault)
    {
        if (faultsList.contains(fault)) faultsList.remove(fault);
    }
    
    /**
     * Confirms if the report has been moderated
     * @return the boolean answer
     */
    public boolean isReportmoderated() {
        return reportmoderated;
    }

    /**
     * Flags the report as moderated
     * @param reportmoderated true - moderated / false - not moderated
     */
    public void setReportmoderated(boolean reportmoderated) {
        this.reportmoderated = reportmoderated;
    }
    
    /**
     * get the filename of the file the report is about
     * @return provides the filename for associated file
     */
    public String getFilename()
    {
        return file.getName();
    }
    
    /**
     * Each fault in the report (1 per line of the report)
     */
    public class Fault
    {
        // description of fault
        private String description;
        
        // position of the fault
        private String position;
        
        // severity of the fault
        private int severity;
        
        // moderated severity of the fault
        private int moderatedSeverity;
        
        // comments added by QC Leader about fault
        private String moderatedComments;
        
        // if the fault has been moderated
        private boolean isModerated = false;
        
        /**
         * Create a fault
         * @param description the description to assign
         * @param position the position of the fault to assign
         * @param severity the severity of the fault to assign
         */
        public Fault(String description, String position, int severity)
        {
            this.description = description;
            this.position = position;
            this.severity = severity;
            this.moderatedSeverity = severity;
        }

        /**
         * Provide the description
         * @return String description
         */
        public String getDescription() {
            return description;
        }

        /**
         * Sets the faults description
         * @param description the description to set
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * Provides the position of the fault
         * @return the position
         */
        public String getPosition() {
            return position;
        }

        /**
         * Sets the position of the fault
         * @param position the position to set
         */
        public void setPosition(String position) {
            this.position = position;
        }

        /**
         * Provides the severity of the fault
         * @return the severity
         */
        public int getSeverity() {
            return severity;
        }

        /**
         * Sets the severity of the the fault
         * @param severity the severity to set
         */
        public void setSeverity(int severity) {
            this.severity = severity;
        }

        /**
         * Provides the moderated severity of the fault
         * @return the Severity
         */
        public int getModeratedSeverity() {
            return moderatedSeverity;
        }

        /**
         * Sets the moderated severity of the fault
         * @param moderatedSeverity the severity to set
         */
        public void setModeratedSeverity(int moderatedSeverity) {
            this.moderatedSeverity = moderatedSeverity;
             isModerated = true;
        }

        /**
         * Provides the QC Leaders moderated comments
         * @return the comments
         */
        public String getModeratedComments() {
            return moderatedComments;
        }

        /**
         * Sets the QC Leaders moderated comments
         * @param moderatedComments the comments 
         */
        public void setModeratedComments(String moderatedComments) {
            this.moderatedComments = moderatedComments;
            isModerated = true;
        }
        
        /**
         * Confirms if this fault has been moderated
         * @return boolean answer
         */
        public boolean getModerated()
        {
            return isModerated;
        }
    }
}
