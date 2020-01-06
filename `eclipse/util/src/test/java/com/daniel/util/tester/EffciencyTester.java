package com.daniel.util.tester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.daniel.util.debug.TimeKeeper;
import com.daniel.util.entity.Complex;
import com.daniel.util.entity.Student;
import com.daniel.util.json.PojoUtil;

public class EffciencyTester {

  // [start]* ################################ 调用方法 ################################ */
  @SuppressWarnings("unused")
  private static List<Map<String, Object>> genStuMaps(int count) {
    List<Map<String, Object>> stuMaps = new ArrayList<Map<String, Object>>();
    for (int ix = 0; ix < count; ix++) {
      Map<String, Object> map1 = new HashMap<String, Object>();
      map1.put("name", "STU" + ix);
      map1.put("age", ix + 1);
      map1.put("sex", (ix & 1) == 1 ? "Male" : "Female");
      stuMaps.add(new HashMap<String, Object>(map1));
    }
    return stuMaps;
  }

  @SuppressWarnings("unused")
  private static List<Student> genStus(int count) {
    List<Student> stus = new ArrayList<Student>();
    for (int ix = 0; ix < count; ix++) {
      Student stu = new Student("STU" + ix, ix + 1, (ix & 1) == 1 ? "Male" : "Female");
      stus.add(stu);
    }
    return stus;
  }

  @SuppressWarnings("unused")
  private static List<Map<String, Object>> genComplexMaps(int count) {
    List<Map<String, Object>> stuMaps = new ArrayList<Map<String, Object>>();
    for (int ix = 0; ix < count; ix++) {
      Map<String, Object> map = new HashMap<String, Object>();
      for (int fieldIx = 0; fieldIx < 100; fieldIx++) {
        map.put("field" + fieldIx, "field" + fieldIx + ": " + ix);
      }
      stuMaps.add(new HashMap<String, Object>(map));
    }
    return stuMaps;
  }

  @SuppressWarnings("unused")
  private static List<Complex> genComplexs(int count) {
    List<Complex> complexs = PojoUtil.parses(genComplexMaps(count), Complex.class);
    return complexs;
  }

  private static <T, V> List<Student> parsesStudent(List<Map<String, V>> maps) {
    List<Student> pojos = new ArrayList<Student>();
    for (Map<String, V> map : maps) {
      String name = (String) map.get("name");
      int age = (int) map.get("age");
      String sex = (String) map.get("sex");
      pojos.add(new Student(name, age, sex));
    }
    return pojos;
  }

  private static <T, V> List<Complex> parsesComplex(List<Map<String, Object>> maps) {
    List<Complex> pojos = new ArrayList<Complex>();
    for (Map<String, Object> map : maps) {
      Complex pojo = new Complex();
//      // [start]* ################################ complex ################################ */
//      String field0 = (String) map.get("field0");pojo.setField0(field0);
//      String field1 = (String) map.get("field1");pojo.setField1(field1);
//      String field2 = (String) map.get("field2");pojo.setField2(field2);
//      String field3 = (String) map.get("field3");pojo.setField3(field3);
//      String field4 = (String) map.get("field4");pojo.setField4(field4);
//      String field5 = (String) map.get("field5");pojo.setField5(field5);
//      String field6 = (String) map.get("field6");pojo.setField6(field6);
//      String field7 = (String) map.get("field7");pojo.setField7(field7);
//      String field8 = (String) map.get("field8");pojo.setField8(field8);
//      String field9 = (String) map.get("field9");pojo.setField9(field9);
//      String field10 = (String) map.get("field10");pojo.setField10(field10);
//      String field11 = (String) map.get("field11");pojo.setField11(field11);
//      String field12 = (String) map.get("field12");pojo.setField12(field12);
//      String field13 = (String) map.get("field13");pojo.setField13(field13);
//      String field14 = (String) map.get("field14");pojo.setField14(field14);
//      String field15 = (String) map.get("field15");pojo.setField15(field15);
//      String field16 = (String) map.get("field16");pojo.setField16(field16);
//      String field17 = (String) map.get("field17");pojo.setField17(field17);
//      String field18 = (String) map.get("field18");pojo.setField18(field18);
//      String field19 = (String) map.get("field19");pojo.setField19(field19);
//      String field20 = (String) map.get("field20");pojo.setField20(field20);
//      String field21 = (String) map.get("field21");pojo.setField21(field21);
//      String field22 = (String) map.get("field22");pojo.setField22(field22);
//      String field23 = (String) map.get("field23");pojo.setField23(field23);
//      String field24 = (String) map.get("field24");pojo.setField24(field24);
//      String field25 = (String) map.get("field25");pojo.setField25(field25);
//      String field26 = (String) map.get("field26");pojo.setField26(field26);
//      String field27 = (String) map.get("field27");pojo.setField27(field27);
//      String field28 = (String) map.get("field28");pojo.setField28(field28);
//      String field29 = (String) map.get("field29");pojo.setField29(field29);
//      String field30 = (String) map.get("field30");pojo.setField30(field30);
//      String field31 = (String) map.get("field31");pojo.setField31(field31);
//      String field32 = (String) map.get("field32");pojo.setField32(field32);
//      String field33 = (String) map.get("field33");pojo.setField33(field33);
//      String field34 = (String) map.get("field34");pojo.setField34(field34);
//      String field35 = (String) map.get("field35");pojo.setField35(field35);
//      String field36 = (String) map.get("field36");pojo.setField36(field36);
//      String field37 = (String) map.get("field37");pojo.setField37(field37);
//      String field38 = (String) map.get("field38");pojo.setField38(field38);
//      String field39 = (String) map.get("field39");pojo.setField39(field39);
//      String field40 = (String) map.get("field40");pojo.setField40(field40);
//      String field41 = (String) map.get("field41");pojo.setField41(field41);
//      String field42 = (String) map.get("field42");pojo.setField42(field42);
//      String field43 = (String) map.get("field43");pojo.setField43(field43);
//      String field44 = (String) map.get("field44");pojo.setField44(field44);
//      String field45 = (String) map.get("field45");pojo.setField45(field45);
//      String field46 = (String) map.get("field46");pojo.setField46(field46);
//      String field47 = (String) map.get("field47");pojo.setField47(field47);
//      String field48 = (String) map.get("field48");pojo.setField48(field48);
//      String field49 = (String) map.get("field49");pojo.setField49(field49);
//      String field50 = (String) map.get("field50");pojo.setField50(field50);
//      String field51 = (String) map.get("field51");pojo.setField51(field51);
//      String field52 = (String) map.get("field52");pojo.setField52(field52);
//      String field53 = (String) map.get("field53");pojo.setField53(field53);
//      String field54 = (String) map.get("field54");pojo.setField54(field54);
//      String field55 = (String) map.get("field55");pojo.setField55(field55);
//      String field56 = (String) map.get("field56");pojo.setField56(field56);
//      String field57 = (String) map.get("field57");pojo.setField57(field57);
//      String field58 = (String) map.get("field58");pojo.setField58(field58);
//      String field59 = (String) map.get("field59");pojo.setField59(field59);
//      String field60 = (String) map.get("field60");pojo.setField60(field60);
//      String field61 = (String) map.get("field61");pojo.setField61(field61);
//      String field62 = (String) map.get("field62");pojo.setField62(field62);
//      String field63 = (String) map.get("field63");pojo.setField63(field63);
//      String field64 = (String) map.get("field64");pojo.setField64(field64);
//      String field65 = (String) map.get("field65");pojo.setField65(field65);
//      String field66 = (String) map.get("field66");pojo.setField66(field66);
//      String field67 = (String) map.get("field67");pojo.setField67(field67);
//      String field68 = (String) map.get("field68");pojo.setField68(field68);
//      String field69 = (String) map.get("field69");pojo.setField69(field69);
//      String field70 = (String) map.get("field70");pojo.setField70(field70);
//      String field71 = (String) map.get("field71");pojo.setField71(field71);
//      String field72 = (String) map.get("field72");pojo.setField72(field72);
//      String field73 = (String) map.get("field73");pojo.setField73(field73);
//      String field74 = (String) map.get("field74");pojo.setField74(field74);
//      String field75 = (String) map.get("field75");pojo.setField75(field75);
//      String field76 = (String) map.get("field76");pojo.setField76(field76);
//      String field77 = (String) map.get("field77");pojo.setField77(field77);
//      String field78 = (String) map.get("field78");pojo.setField78(field78);
//      String field79 = (String) map.get("field79");pojo.setField79(field79);
//      String field80 = (String) map.get("field80");pojo.setField80(field80);
//      String field81 = (String) map.get("field81");pojo.setField81(field81);
//      String field82 = (String) map.get("field82");pojo.setField82(field82);
//      String field83 = (String) map.get("field83");pojo.setField83(field83);
//      String field84 = (String) map.get("field84");pojo.setField84(field84);
//      String field85 = (String) map.get("field85");pojo.setField85(field85);
//      String field86 = (String) map.get("field86");pojo.setField86(field86);
//      String field87 = (String) map.get("field87");pojo.setField87(field87);
//      String field88 = (String) map.get("field88");pojo.setField88(field88);
//      String field89 = (String) map.get("field89");pojo.setField89(field89);
//      String field90 = (String) map.get("field90");pojo.setField90(field90);
//      String field91 = (String) map.get("field91");pojo.setField91(field91);
//      String field92 = (String) map.get("field92");pojo.setField92(field92);
//      String field93 = (String) map.get("field93");pojo.setField93(field93);
//      String field94 = (String) map.get("field94");pojo.setField94(field94);
//      String field95 = (String) map.get("field95");pojo.setField95(field95);
//      String field96 = (String) map.get("field96");pojo.setField96(field96);
//      String field97 = (String) map.get("field97");pojo.setField97(field97);
//      String field98 = (String) map.get("field98");pojo.setField98(field98);
//      String field99 = (String) map.get("field99");pojo.setField99(field99);
//      // [end]
      // [start]* ################################ complex2 ################################ */
      pojo.setField0("field0");
      pojo.setField1("field1");
      pojo.setField2("field2");
      pojo.setField3("field3");
      pojo.setField4("field4");
      pojo.setField5("field5");
      pojo.setField6("field6");
      pojo.setField7("field7");
      pojo.setField8("field8");
      pojo.setField9("field9");
      pojo.setField10("field10");
      pojo.setField11("field11");
      pojo.setField12("field12");
      pojo.setField13("field13");
      pojo.setField14("field14");
      pojo.setField15("field15");
      pojo.setField16("field16");
      pojo.setField17("field17");
      pojo.setField18("field18");
      pojo.setField19("field19");
      pojo.setField20("field20");
      pojo.setField21("field21");
      pojo.setField22("field22");
      pojo.setField23("field23");
      pojo.setField24("field24");
      pojo.setField25("field25");
      pojo.setField26("field26");
      pojo.setField27("field27");
      pojo.setField28("field28");
      pojo.setField29("field29");
      pojo.setField30("field30");
      pojo.setField31("field31");
      pojo.setField32("field32");
      pojo.setField33("field33");
      pojo.setField34("field34");
      pojo.setField35("field35");
      pojo.setField36("field36");
      pojo.setField37("field37");
      pojo.setField38("field38");
      pojo.setField39("field39");
      pojo.setField40("field40");
      pojo.setField41("field41");
      pojo.setField42("field42");
      pojo.setField43("field43");
      pojo.setField44("field44");
      pojo.setField45("field45");
      pojo.setField46("field46");
      pojo.setField47("field47");
      pojo.setField48("field48");
      pojo.setField49("field49");
      pojo.setField50("field50");
      pojo.setField51("field51");
      pojo.setField52("field52");
      pojo.setField53("field53");
      pojo.setField54("field54");
      pojo.setField55("field55");
      pojo.setField56("field56");
      pojo.setField57("field57");
      pojo.setField58("field58");
      pojo.setField59("field59");
      pojo.setField60("field60");
      pojo.setField61("field61");
      pojo.setField62("field62");
      pojo.setField63("field63");
      pojo.setField64("field64");
      pojo.setField65("field65");
      pojo.setField66("field66");
      pojo.setField67("field67");
      pojo.setField68("field68");
      pojo.setField69("field69");
      pojo.setField70("field70");
      pojo.setField71("field71");
      pojo.setField72("field72");
      pojo.setField73("field73");
      pojo.setField74("field74");
      pojo.setField75("field75");
      pojo.setField76("field76");
      pojo.setField77("field77");
      pojo.setField78("field78");
      pojo.setField79("field79");
      pojo.setField80("field80");
      pojo.setField81("field81");
      pojo.setField82("field82");
      pojo.setField83("field83");
      pojo.setField84("field84");
      pojo.setField85("field85");
      pojo.setField86("field86");
      pojo.setField87("field87");
      pojo.setField88("field88");
      pojo.setField89("field89");
      pojo.setField90("field90");
      pojo.setField91("field91");
      pojo.setField92("field92");
      pojo.setField93("field93");
      pojo.setField94("field94");
      pojo.setField95("field95");
      pojo.setField96("field96");
      pojo.setField97("field97");
      pojo.setField98("field98");
      pojo.setField99("field99");

      // [end]
      pojos.add(pojo);
    }
    return pojos;
  }

  // [end]

//  @Test
  public void test1() throws Exception {
    List<Map<String, Object>> stuMaps1 = genStuMaps(1000000);
    List<Map<String, Object>> stuMaps2 = genStuMaps(1000000);
    List<Map<String, Object>> stuMaps3 = genStuMaps(1000000);
    TimeKeeper.step(0);
    List<Student> stus1 = PojoUtil.parses(stuMaps1, Student.class);
    TimeKeeper.step(1);
    List<Student> stus3 = parsesStudent(stuMaps2);
    TimeKeeper.step(2);
  }

//  @Test
  public void test2() throws Exception {
    List<Map<String, Object>> compMaps1 = genComplexMaps(10000);
    List<Map<String, Object>> compMaps2 = genComplexMaps(10000);
    TimeKeeper.step(0);
    List<Complex> complexs1 = PojoUtil.parses(compMaps1, Complex.class);
    TimeKeeper.step(2);
    List<Complex> complexs3 = parsesComplex(compMaps2);
    TimeKeeper.step(3);
  }
  
//  @Test
  public void test3() throws Exception {
    List<Complex> complexs1 = genComplexs(10000);
    List<Map<String, Object>> compMaps2 = genComplexMaps(10000);
    TimeKeeper.step(0);
    List<Complex> complexs2 = PojoUtil.parses(compMaps2, Complex.class);
    TimeKeeper.step(2);
    List<Map<String, Object>> compMaps1 = PojoUtil.mapifys(complexs1);
    TimeKeeper.step(1);

    System.out.println(compMaps1.subList(0, 2));
    System.out.println(complexs2.subList(0, 2));
  }

//  @Test
  public void test4() throws Exception {
    List<Student> stus1 = genStus(1000000);
    List<Map<String, Object>> stuMaps2 = genStuMaps(1000000);
    TimeKeeper.step(0);
    List<Student> stus2 = PojoUtil.parses(stuMaps2, Student.class);
    TimeKeeper.step(2);
    List<Map<String, Object>> stuMaps1 = PojoUtil.mapifys(stus1);
    TimeKeeper.step(1);
    System.out.println(stuMaps1.subList(0, 2));
    System.out.println(stus2.subList(0, 2));
  }
  
  @Test
  public void test5() throws Exception {

    return;
  }
  

}
