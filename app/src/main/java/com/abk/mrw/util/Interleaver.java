package com.abk.mrw.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.support.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

/**
 * Provides an output Iterable that interleaves from input Iterables.
 * <p/>
 * Example: [[a1, a2, a3] [b1] [] [d1, d2]] -> [a1, b1, d1, a2, d2, a3]
 */
public class Interleaver {

    private Interleaver() {
    }

    /**
     * Interleave the supplied iterables.
     *
     * @param input
     * @return
     */
    public static <T> Iterable<T> fromIterables(Iterable<? extends Iterable<T>> input) {

        return new InterleavingIterable<T>(
                Lists.newArrayList(
                        Iterables.transform(input,
                                new Function<Iterable<T>, Iterator<T>>() {

                                    @Override
                                    public Iterator<T> apply(@Nullable Iterable<T> input) {
                                        return input.iterator();
                                    }
                                })));
    }

    /**
     * Interleave the supplied iterators.
     *
     * @param input
     * @return
     */
    public static <T> Iterable<T> fromIterators(Collection<? extends Iterator<T>> input) {
        return new InterleavingIterable<T>(input);
    }

    private static class InterleavingIterable<T> implements Iterable<T> {

        private final Iterable<? extends Iterator<T>> columns;
        private Iterator<T> row;
        private Iterator<? extends Iterator<T>> columnIterator;
        private final List<T> rowBuffer;

        public InterleavingIterable(final Collection<? extends Iterator<T>> input) {
            this.columns = input;
            this.row = Iterators.emptyIterator();
            this.columnIterator = columns.iterator();
            this.rowBuffer = new ArrayList<T>(Iterables.size(input));
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {

                @Override
                public boolean hasNext() {
                    if (!row.hasNext()) {
                        row = getNextSlice();
                    }

                    return row.hasNext();
                }

                @Override
                public T next() {
                    return row.next();
                }

                @Override
                public void remove() {
                    row.remove();
                }

                /**
                 * @return an iterator of the head elements of each input.
                 */
                private Iterator<T> getNextSlice() {
                    //Re-use existing list to avoid object allocation.
                    rowBuffer.clear();

                    //Visit the head of each input column and consume it if not empty.
                    while (columnIterator.hasNext()) {
                        Iterator<T> column = columnIterator.next();

                        //Some input iterables may be emptied before others.
                        if (column.hasNext()) {
                            //Populate buffer of head elements for return
                            rowBuffer.add(column.next());
                        }
                    }

                    // Set to the next row.
                    columnIterator = columns.iterator();

                    // Return the current row.
                    return rowBuffer.iterator();
                }
            };
        }
    }
}
