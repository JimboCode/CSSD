package BLL;

/**
 * Media asset and element status
 * 
 * @author James Staite
 */
public enum MediaStatus 
{
    AWAITING_ACTION,
    ORDERED_FROM_CONTRACTOR,
    REQUESTED_FROM_CLIENT,
    ORDERED_IN_HOUSE,
    ARRIVED_IN_VAULT,
    INWARD_QC,
    QC_REPORT_AVALIABLE,
    FIXES_ORDERED_DELAYED,
    REORDERED_FROM_CONTRACTOR,
    REPLACEMENT_REQUESTED_FROM_CLIENT,
    FIXES_COMPLETED,
    AWAITING_QC,
    APPROVED_FOR_COMPRESSION,
    COMPRESSION_COMPLETED,
    ASSET_READY,
    AWAITING_ASSETS,
    AWAITING_ASSETS_DELAYED,
    ALL_ASSETS_AVALIABLE,
    QUICK_REQUESTED,
    QUICK_CREATED,
    BUILD_COMPLETE,
    APPROVED_FOR_FINAL_COMPRESSION,
    SCRUBBED_FROM_DISC,
    NONE; // only for use in combo box filtering not a valid status!!!!!!!
    
    /**
     * Provides a displayable string of the enumeration
     * @return displayable string
     */
    @Override
    public String toString()
    {
        // strips the _ and replaces with " "
        // Capitalises the first letter
        // Replaces QC with Quality Control
        String value = super.toString();
        String words[] = value.split("_");
        value = "";
        for(String word: words)
        {
            word = word.toUpperCase().replace(word.substring(1), word.substring(1).toLowerCase());
            value += word + " ";
        }
        value = value.replace("Qc", "QC");
        value = value.replace("Delayed", "(Delayed)");
        return value.trim();
    }
}
