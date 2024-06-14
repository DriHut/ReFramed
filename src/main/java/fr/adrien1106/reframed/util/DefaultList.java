package fr.adrien1106.reframed.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class DefaultList<T> implements List<T> {

    protected T default_value;
    protected List<T> elements;

    public DefaultList(T default_value, List<T> elements) {
        this.default_value = default_value;
        this.elements = elements;
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return elements.contains(o) || default_value.equals(o);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return elements.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return elements.toArray();
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        return elements.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return elements.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return elements.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return elements.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        return elements.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        return elements.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return elements.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return elements.retainAll(c);
    }

    @Override
    public void clear() {
        elements.clear();
    }

    @Override
    public T get(int index) {
        return index < elements.size() && index >= 0 ? elements.get(index) : default_value;
    }

    @Override
    public T set(int index, T element) {
        return elements.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        elements.add(index, element);
    }

    @Override
    public T remove(int index) {
        return elements.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return elements.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return elements.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator() {
        return elements.listIterator();
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return elements.listIterator();
    }

    @NotNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return elements.subList(fromIndex, toIndex);
    }
}
