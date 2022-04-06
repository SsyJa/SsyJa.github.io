package RMIServer;

import java.io.Serializable;

public class MyBook implements Serializable {
    private static final long serialVersionUID = 1L;
//JAVA序列化的机制是通过 判断类的serialVersionUID来验证的版本一致的
    private static int SUM;
    private int ID;
    private String name;

    public MyBook(String name) {
        this.name = name;
        ID = SUM++;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ID：" + ID + ", name：" + name ;
    }


}


//serialVersionUID适用于java序列化机制。简单来说，JAVA序列化的机制是通过判断类的serialVersionUID来验证的版本一致的。
//在进行反序列化时，JVM会把传来的字节流中的serialVersionUID于本地相应实体类的serialVersionUID进行比较。
//如果相同说明是一致的，可以进行反序列化，否则会出现反序列化版本一致的异常，即是InvalidCastException。
