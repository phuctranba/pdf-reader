package core.pdf;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public class PDFDoc implements Serializable {
    String id;
    String name;
    String path;
    String size;
    boolean favourite;
    Date openedTime, lastModified;

    public static Comparator<PDFDoc> sortNameDecrease = new Comparator<PDFDoc>() {
        public int compare(PDFDoc pdfDoc1, PDFDoc pdfDoc2) {
            return pdfDoc1.name.toLowerCase().compareTo(pdfDoc2.name.toLowerCase());
        }};

    public static Comparator<PDFDoc> sortNameIncrease = new Comparator<PDFDoc>() {
        public int compare(PDFDoc pdfDoc1, PDFDoc pdfDoc2) {
            return pdfDoc2.name.toLowerCase().compareTo(pdfDoc1.name.toLowerCase());
        }};

    public static Comparator<PDFDoc> sortTimeIncrease = new Comparator<PDFDoc>() {
        public int compare(PDFDoc pdfDoc1, PDFDoc pdfDoc2) {
            return pdfDoc2.lastModified.compareTo(pdfDoc1.lastModified);
        }};

    public static Comparator<PDFDoc> sortTimeDecrease = new Comparator<PDFDoc>() {
        public int compare(PDFDoc pdfDoc1, PDFDoc pdfDoc2) {
            return pdfDoc1.lastModified.compareTo(pdfDoc2.lastModified);
        }};

    public static Comparator<PDFDoc> sortOpendTime = new Comparator<PDFDoc>() {
        public int compare(PDFDoc pdfDoc1, PDFDoc pdfDoc2) {
            return pdfDoc1.openedTime.compareTo(pdfDoc2.openedTime);
        }};

    public static Comparator<PDFDoc> sortSizeIncrease = new Comparator<PDFDoc>() {
        public int compare(PDFDoc pdfDoc1, PDFDoc pdfDoc2) {
            return (int) (Double.parseDouble(pdfDoc2.getSize())-Double.parseDouble(pdfDoc1.getSize()));
        }};

    public static Comparator<PDFDoc> sortSizeDecrease = new Comparator<PDFDoc>() {
        public int compare(PDFDoc pdfDoc1, PDFDoc pdfDoc2) {
            return (int) (Double.parseDouble(pdfDoc1.getSize())-Double.parseDouble(pdfDoc2.getSize()));
        }};

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public Date getOpenedTime() {
        return openedTime;
    }

    public void setOpenedTime(Date openedTime) {
        this.openedTime = openedTime;
    }

    @Override
    public String toString() {
        return "PDFDoc{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", size='" + size + '\'' +
                '}';
    }
}
