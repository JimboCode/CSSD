package BLL;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

/**
 *
 * @author James Staite
 */
public class QCReport 
{
    private EventList<Fault> faultsList = GlazedLists.threadSafeList(new BasicEventList<Fault>());
    
    private boolean reportmoderated = false;
    
    public EventList<Fault> getReportList()
    {
        return faultsList;
    }
    
    public void addFault(String description, String position, int severity)
    {
        Fault newfault = new Fault(description, position, severity);
        faultsList.add(newfault);
    }
    
    public void removeFault(Fault fault)
    {
        if (faultsList.contains(fault)) faultsList.remove(fault);
    }
    
    /**
         * @return the reportmoderated
         */
        public boolean isReportmoderated() {
            return reportmoderated;
        }

        /**
         * @param reportmoderated the reportmoderated to set
         */
        public void setReportmoderated(boolean reportmoderated) {
            this.reportmoderated = reportmoderated;
        }
    
    public class Fault
    {
        private String description;
        private String position;
        private int severity;
        private int moderatedSeverity;
        private String moderatedComments;
        private boolean isModerated = false;
        
        public Fault(String description, String position, int severity)
        {
            this.description = description;
            this.position = position;
            this.severity = severity;
            this.moderatedSeverity = severity;
        }

        /**
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * @param description the description to set
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * @return the position
         */
        public String getPosition() {
            return position;
        }

        /**
         * @param position the position to set
         */
        public void setPosition(String position) {
            this.position = position;
        }

        /**
         * @return the severity
         */
        public int getSeverity() {
            return severity;
        }

        /**
         * @param severity the severity to set
         */
        public void setSeverity(int severity) {
            this.severity = severity;
        }

        /**
         * @return the moderatedSeverity
         */
        public int getModeratedSeverity() {
            return moderatedSeverity;
        }

        /**
         * @param moderatedSeverity the moderatedSeverity to set
         */
        public void setModeratedSeverity(int moderatedSeverity) {
            this.moderatedSeverity = moderatedSeverity;
             isModerated = true;
        }

        /**
         * @return the moderatedComments
         */
        public String getModeratedComments() {
            return moderatedComments;
        }

        /**
         * @param moderatedComments the moderatedComments to set
         */
        public void setModeratedComments(String moderatedComments) {
            this.moderatedComments = moderatedComments;
            isModerated = true;
        }
        
        public boolean getModerated()
        {
            return isModerated;
        }
    }
}
