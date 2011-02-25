package com.griddynamics.gemfire.serialization._jdktypes;

import com.griddynamics.gemfire.serialization.HierarchyRegistry;
import com.griddynamics.gemfire.serialization.TestParent;
import javassist.CannotCompileException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.InvalidClassException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

/**
 * @author igolovach
 */

public class JdkTypesTest extends TestParent {

    @BeforeClass
    public void before() throws InvalidClassException, CannotCompileException {
        // register
        HierarchyRegistry.registerAll(getContextClassLoader(), JdkTypesBean.class);
    }

    @DataProvider(name = "data")
    public Object[][] data() throws UnknownHostException {
        return new Object[][]{
                // null fields
               // new Object[]{new JdkTypesBean()},
                // initialize diff field groups of bean
                new Object[]{produceCommon()},
                new Object[]{produceOldCollections()},
                new Object[]{produceCollectionsAPILists()},
                new Object[]{produceCollectionsAPIMaps()},
                new Object[]{produceCollectionsAPISets()},
        };
    }

    @Test(dataProvider = "data")
    public void test(JdkTypesBean expected) {

        // Serialize / Deserialize
        JdkTypesBean actual = serializeAndDeserialize(expected);

        // assert
        assert actual.equals(expected);
    }

    private JdkTypesBean produceCommon() throws UnknownHostException {
        final JdkTypesBean result = new JdkTypesBean();

        result.setObject(123);
        result.setClazz(Integer.class);
        result.setString("Hello");
        result.setDate(new Date(123456));
        //result.setFile(new File("c:/a/b/c.txt")); //todo: but "/a/b/c.txt" - not! see how realized file transfer in GemFire
        result.setInetAddress(InetAddress.getByAddress(new byte[]{1, 2, 3, 4}));
        result.setInet4Address((Inet4Address) InetAddress.getByAddress(new byte[]{1, 2, 3, 4}));
        result.setInet6Address(Inet6Address.getByAddress("hello", new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6}, 1));
        // properties
        final Properties properties = new Properties();
        properties.setProperty("abc", "def");
        result.setProperties(properties);

        return result;
    }

    private JdkTypesBean produceOldCollections() {
        final JdkTypesBean result = new JdkTypesBean();

        // Hashtable
        final Hashtable<Object, Object> hashtable = new Hashtable<Object, Object>();
        hashtable.put("123", 123);
        result.setHashtable(hashtable);
        // Vector
        final Vector<Object> vector = new Vector<Object>();
        vector.add("456");
        result.setVector(vector);
        // Stack
        final Stack<Object> stack = new Stack<Object>();
        stack.add("789");
        result.setStack(stack);

        return result;
    }

    private JdkTypesBean produceCollectionsAPILists() {
        final JdkTypesBean result = new JdkTypesBean();

        // List
        final List<Object> list = new ArrayList<Object>();
        list.add("123");
        result.setList(list);
        // ArrayList
        final ArrayList<Object> arrayList = new ArrayList<Object>();
        arrayList.add("456");
        result.setArrayList(arrayList);
        // ArrayList
        final LinkedList<Object> linkedList = new LinkedList<Object>();
        linkedList.add("789");
        result.setLinkedList(linkedList);

        return result;
    }

    private JdkTypesBean produceCollectionsAPIMaps() {
        final JdkTypesBean result = new JdkTypesBean();

        // Map
        final Map<Object, Object> map = new HashMap<Object, Object>();
        map.put("123", 456.789d);
        result.setMap(map);
        // HashMap
        final HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
        hashMap.put("123", 456.789d);
        result.setHashMap(hashMap);
        // HashMap
        final TreeMap<Object, Object> treeMap = new TreeMap<Object, Object>();
        treeMap.put("123", 456.789d);
        result.setTreeMap(treeMap);
        // Map
        final IdentityHashMap<Object, Object> identityHashMap = new IdentityHashMap<Object, Object>();
        identityHashMap.put("123", 456.789d);
        result.setIdentityHashMap(identityHashMap);

        return result;
    }

    private JdkTypesBean produceCollectionsAPISets() {
        final JdkTypesBean result = new JdkTypesBean();

        // Set
        final Set<Object> set = new HashSet<Object>();
        set.add("123");
        result.setSet(set);
        // Set
        final LinkedHashSet<Object> linkedHashSet = new LinkedHashSet<Object>();
        linkedHashSet.add("123");
        result.setLinkedHashSet(linkedHashSet);
        // HashSet
        final HashSet<Object> hashSet = new HashSet<Object>();
        hashSet.add("123");
        result.setHashSet(hashSet);
        // Set
        final TreeSet<Object> treeSet = new TreeSet<Object>();
        treeSet.add("123");
        result.setTreeSet(treeSet);

        return result;
    }
}

