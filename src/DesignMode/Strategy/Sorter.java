package DesignMode.Strategy;

import java.util.Comparator;

public class Sorter<T> {
    public void sorter(T[] data, Comparator com){
        int i, j;
        for (i = 1; i < data.length; i++)
        {
            T d = data[i];
            for (j = i - 1; j >= 0 && com.compare(data[j], d) > 0; j--)
            {
                data[j + 1] = data[j];
            }
            data[j + 1] = d;
        }
    }
}
