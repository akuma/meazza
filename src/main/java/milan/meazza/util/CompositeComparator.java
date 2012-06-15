/* 
 * @(#)CompositeComparator.java    Created on 2012-05-31
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package milan.meazza.util;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * 用来解决字段的组合排列问题的比较器。
 * 
 * <p>
 * 运用了组合（Compositor）模式：把一些具有不同功能的类组合起来成一个类或者数组， 然后通过调用某个方法来循环数组中的所有元素，最后返回用户所需要的结果。
 * 
 * @author akuma
 */
public class CompositeComparator<T> implements Comparator<T> {

    /**
     * 比较器列表, 越排在列表前面的比较器优先级越高.
     */
    private List<Comparator<T>> comparatorList = new LinkedList<Comparator<T>>();

    /**
     * 获取比较器列表.
     */
    public List<Comparator<T>> getComparatorList() {
        return comparatorList;
    }

    /**
     * 添加一个比较器到比较器列表中.
     */
    public void addComparator(Comparator<T> comparator) {
        if (comparator == null) {
            return;
        }

        comparatorList.add(comparator);
    }

    /**
     * 添加多个比较器到比较器列表中.
     */
    public void addComparators(Comparator<T>[] comparators) {
        if (comparators == null) {
            return;
        }

        for (Comparator<T> comparator : comparators) {
            comparatorList.add(comparator);
        }
    }

    /**
     * 根据比较器列表中的比较器的优先级来对对象进行排序. 优先级高的比较器会先被使用.<br>
     * 如果两对象比较结果相等, 则会继续使用其次优先级的比较器进行比较.
     */
    @Override
    public int compare(T o1, T o2) {
        for (Comparator<T> comparator : comparatorList) {
            int result = comparator.compare(o1, o2);
            if (result != 0) {
                return result;
            }
        }

        return 0;
    }

}
