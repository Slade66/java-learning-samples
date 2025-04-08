package com.slade66;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.io.*;

public class FileTest {

    @Test
    public void FileApiTest() throws IOException {
        String baseDir = "E:\\";
        String filename = "a.txt";
        File file = new File(baseDir, filename);
        file.createNewFile();
        System.out.println(file.getName());
        System.out.println(file.getAbsolutePath());
        System.out.println(file.getParent());
        System.out.println(file.length());
        System.out.println(file.isFile());
        System.out.println(file.isDirectory());
        System.out.println(file.exists());
        System.out.println(file.lastModified());
    }

    @Test
    public void FileInputStreamTest() {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream("E:\\a.txt");
            byte[] tmp = new byte[5];
            int readLen = 0;
            while ((readLen = fileInputStream.read(tmp)) != -1) {
                System.out.println(new String(tmp, 0, readLen));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Test
    public void FileOutputStreamTest() {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream("E:\\a.txt");
            fileOutputStream = new FileOutputStream("E:\\b.txt");
            int readData = 0;
            while ((readData = fileInputStream.read()) != -1) {
                fileOutputStream.write(readData);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void FileReaderTest() {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader("E:\\a.txt");
            int readData = 0;
            while ((readData = fileReader.read()) != -1) {
                System.out.print((char) readData);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void FileWriterTest() {
        FileWriter fileWriter = null;
        String data = "lxd sb 250";
        try {
            fileWriter = new FileWriter("E:\\a2.txt");
            fileWriter.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void BufferReaderTest() throws IOException {
        FileReader fileReader = new FileReader("E:\\a.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String readData = null;
        while ((readData = bufferedReader.readLine()) != null) {
            System.out.println(readData);
        }
    }

    @Test
    public void BufferWriterTest() throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("E:\\a.txt"));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("E:\\e.txt"));
        String readData = null;
        while ((readData = bufferedReader.readLine()) != null) {
            bufferedWriter.write(readData);
        }
        bufferedReader.close();
        bufferedWriter.close();
    }

    @Test
    public void ObjectOutputStreamTest() throws Exception {
        File file = new File("E:\\obj.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

        Person lyz = new Person("lyz", 23);
        objectOutputStream.writeObject(lyz);
    }

    @Test
    public void ObjectInputStreamTest() throws Exception {
        FileInputStream fileInputStream = new FileInputStream("E:\\obj.txt");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        System.out.println(objectInputStream.readObject());
    }

    @Test
    public void PrintStreamTest() throws Exception {
        System.setOut(new PrintStream("E:\\9.txt"));
        PrintStream printStream = System.out;
        printStream.println(1);
    }

}

@ToString
@AllArgsConstructor
class Person implements Serializable {
    private String name;
    private int age;
}
