package engine.engineGpup;

import java.io.File;
import java.io.FileNotFoundException;

public class FileChecker {

    private File xmlFile;
    private String fileName;
    public FileChecker(String newFileName) throws FileNotFoundException {
        fileName=newFileName;
        xmlFile = new File(fileName);

        if(!xmlFile.exists()) {///לזרוק שגיאה הקובץ לא קיים- מה רוצים לעשות? לחזור לתפריט או לבקש קובץ חדש?

        }
        //System.out.println("is exists ? " + xmlFile.exists());//###
        if(!isFileProper()){///לזרוק שגיאה הקובץ לא תקין ואז מה לעשות? לקלוט שם קובץ חדש?

        }
        //System.out.println("is proper ? " + isFileProper());//###

    }
    private boolean isFileProper()throws FileNotFoundException {
        boolean allGood = false;
        String extension = "";
        try {
            if (xmlFile == null) {
                throw new FileNotFoundException("File is NULL.");
            } else if (xmlFile.exists()) {
                throw new FileNotFoundException("File not exist.");
            } else {
                extension = fileName.substring(fileName.lastIndexOf("."));

                if (!extension.equals("xml")) {
                    throw new FileNotFoundException("File is not a XML file.");
                }
                allGood = true;
            }
        }finally {
            return allGood;
        }
    }
}
