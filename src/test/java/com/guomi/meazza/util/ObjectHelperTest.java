/*
 * @(#)ObjectHelperTest.java    Created on 2014年1月24日
 * Copyright (c) 2014 Guomi. All rights reserved.
 */
package com.guomi.meazza.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * @author akuma
 */
public class ObjectHelperTest {

    @Test
    public void testCopyPropertiesSClassOfT() {
        Person person = new Person();
        person.setName("jerry");
        person.setSex(20);
        person.setAlive(true);
        person.setBirthday(new Date());

        Student student = ObjectHelper.copyProperties(person, Student.class);
        assertEquals(person.getName(), student.getName());
        assertEquals(person.getSex(), student.getSex());
        assertEquals(person.isAlive(), student.isAlive());
        assertEquals(person.getBirthday(), student.getBirthday());
    }

    @Test
    public void testCopyPropertiesListOfSClassOfT() {
        Person person1 = new Person();
        person1.setName("jerry");
        person1.setSex(20);
        person1.setAlive(true);
        person1.setBirthday(new Date());

        Person person2 = new Person();
        person2.setName("tom");
        person2.setSex(20);
        person2.setAlive(true);
        person2.setBirthday(new Date());

        List<Person> persons = new ArrayList<>();
        persons.add(person1);
        persons.add(person2);

        List<Student> students = ObjectHelper.copyProperties(persons, Student.class);
        for (int i = 0, n = students.size(); i < n; i++) {
            Person person = persons.get(i);
            Student student = students.get(i);

            assertEquals(person.getName(), student.getName());
            assertEquals(person.getSex(), student.getSex());
            assertEquals(person.isAlive(), student.isAlive());
            assertEquals(person.getBirthday(), student.getBirthday());
        }
    }

    @Test
    public void testCopyPropertiesOfMap() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("name", "jerry");
        data.put("sex", 20);
        data.put("alive", "true");
        data.put("birthday", new Date());

        Person person = ObjectHelper.copyProperties(data, Person.class);
        assertEquals(person.getName(), ObjectHelper.getPropertyValueQuietly(person, "name"));
        assertEquals(person.getSex(), ObjectHelper.getPropertyValueQuietly(person, "sex"));
        assertEquals(person.isAlive(), ObjectHelper.getPropertyValueQuietly(person, "alive"));
        assertEquals(person.getBirthday(), ObjectHelper.getPropertyValueQuietly(person, "birthday"));
        assertEquals(null, ObjectHelper.getPropertyValueQuietly(person, "none"));
    }

    @Test
    public void testGetAndSetPropertyValueQuietly() {
        Person person = new Person();
        person.setName("jerry");
        person.setSex(20);
        person.setAlive(true);
        person.setBirthday(new Date());

        assertEquals(person.getName(), ObjectHelper.getPropertyValueQuietly(person, "name"));
        assertEquals(person.getSex(), ObjectHelper.getPropertyValueQuietly(person, "sex"));
        assertEquals(person.isAlive(), ObjectHelper.getPropertyValueQuietly(person, "alive"));
        assertEquals(person.getBirthday(), ObjectHelper.getPropertyValueQuietly(person, "birthday"));
        assertEquals(null, ObjectHelper.getPropertyValueQuietly(person, "none"));

        ObjectHelper.setPropertyValueQuietly(person, "name", "tom");
        ObjectHelper.setPropertyValueQuietly(person, "sex", 20);
        ObjectHelper.setPropertyValueQuietly(person, "alive", false);
        ObjectHelper.setPropertyValueQuietly(person, "birthday", new Date());
        ObjectHelper.setPropertyValueQuietly(person, "none", "none");

        assertEquals(person.getName(), ObjectHelper.getPropertyValueQuietly(person, "name"));
        assertEquals(person.getSex(), ObjectHelper.getPropertyValueQuietly(person, "sex"));
        assertEquals(person.isAlive(), ObjectHelper.getPropertyValueQuietly(person, "alive"));
        assertEquals(person.getBirthday(), ObjectHelper.getPropertyValueQuietly(person, "birthday"));
        assertEquals(null, ObjectHelper.getPropertyValueQuietly(person, "none"));
    }

    public static class Person {

        private String name;
        private int sex;
        private boolean alive;
        private Date birthday;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public boolean isAlive() {
            return alive;
        }

        public void setAlive(boolean alive) {
            this.alive = alive;
        }

        public Date getBirthday() {
            return birthday;
        }

        public void setBirthday(Date birthday) {
            this.birthday = birthday;
        }

    }

    public static class Student extends Person {

        private int studyNo;
        private String address;

        public int getStudyNo() {
            return studyNo;
        }

        public void setStudyNo(int studyNo) {
            this.studyNo = studyNo;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

    }

}
