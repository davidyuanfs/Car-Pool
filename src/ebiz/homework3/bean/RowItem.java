package ebiz.homework3.bean;

public class RowItem {

	private int imageId;
    private String title;
    private String phone;
    private String desc;
     
    public RowItem(int imageId, String title, String phone, String desc) {
        this.imageId = imageId;
        this.title = title;
        this.phone = phone;
        this.desc = desc;
    }
    
    public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    @Override
    public String toString() {
        return title + "\n" + desc;
    }   
}
