package com.slade66;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class EnumTest {

    @Test
    public void test1() {
        Student lyz = new Student("lyz", Sex.Boy);
        Student lxd = new Student("lxd", Sex.Girl);
        Student lwj = new Student("lwj", Sex.Girl);
        List<Student> classRoom = new ArrayList<>(List.of(lyz, lxd, lwj));
        for (Student student : classRoom) {
            switch (student.getSex()) {
                case Boy:
                    System.out.println(student.getName() + "是男的");
                    break;
                case Girl:
                    System.out.println(student.getName() + "是女的");
                    break;
            }
        }

    }

}

enum Sex {
    Boy, Girl
}

@Data
@AllArgsConstructor
class Student {
    private String name;
    private Sex sex;
}
