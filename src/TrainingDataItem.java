// represents one 'row' of the given data.
public class TrainingDataItem {
	String buying, maint, doors, persons, lug_boot, safety, targetClass;

	public TrainingDataItem(String str) {
		String[] a = str.split(",");
		this.buying = a[0];
		this.maint = a[1];
		this.doors = a[2];
		this.persons = a[3];
		this.lug_boot = a[4];
		this.safety = a[5];
		this.targetClass = a[6];
	}

	public String getBuying() {
		return buying;
	}

	public String getMaint() {
		return maint;
	}

	public String getDoors() {
		return doors;
	}

	public String getPersons() {
		return persons;
	}

	public String getLug_boot() {
		return lug_boot;
	}

	public String getSafety() {
		return safety;
	}

	public String getTargetClass() {
		return targetClass;
	}
}