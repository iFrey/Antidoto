package buy.fair.antidoto;

/**
 * Created by iFrey on 15-Apr-17.
 */

public class Result {

    private String reasonName;
    private String reasonDescription;
    private String reasonLink;

    private String elementName;
    private String elementDescription;
    private int elementBarcode;

    private String companyName;



    public Result(){
        super();
    }

    public Result(String reasonName, String reasonDescription, String reasonLink, String elementName, String elementDescription, int elementBarcode, String companyName) {
        this.elementName = elementName;
        this.elementBarcode = elementBarcode;
        this.elementDescription = elementDescription;
        this.reasonLink = reasonLink;
        this.companyName = companyName;
        this.reasonDescription = reasonDescription;
        this.reasonName = reasonName;
    }

    public void setElementName(String elementName) { this.elementName = elementName; }

    public void setElementBarcode(int elementBarcode) { this.elementBarcode = elementBarcode; }

    public void setElementDescription(String elementDescription) { this.elementDescription = elementDescription; }

    public void setReasonLink(String reasonLink) { this.reasonLink = reasonLink; }

    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public void setReasonName(String reasonName) { this.reasonName = reasonName; }

    public void setReasonDescription(String reasonDescription) { this.reasonDescription = reasonDescription; }


    public String getReasonDescription() { return reasonDescription; }

    public String getReasonName() { return reasonName; }

    public String getElementName() { return elementName; }

    public int getElementBarcode() { return elementBarcode; }

    public String getElementDescription() { return elementDescription; }

    public String getReasonLink() { return reasonLink;  }

    public String getCompanyName() {
        return companyName;
    }


}
