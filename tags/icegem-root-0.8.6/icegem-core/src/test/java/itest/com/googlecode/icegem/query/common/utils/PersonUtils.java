package itest.com.googlecode.icegem.query.common.utils;

import com.gemstone.gemfire.cache.Region;
import itest.com.googlecode.icegem.query.common.model.Person;

import java.util.Arrays;
import java.util.List;

/**
 * Help methods for working with Person entry.
 *
 * @author Andrey Stepanov aka standy
 */
public class PersonUtils {
    /**
     * Checks existence of person with specified social number in results.
     *
     * @param results of type List<?>
     * @param socialNumber of type int
     * @return boolean
     */
    public static boolean containsPersonWithSocialNumber(List<?> results, int socialNumber) {
	for (Object result : results) {
	    if (((Person) result).getSocialNumber() == socialNumber) {
		return true;
	    }
	}

	return false;
    }

    /**
     * Populates region by persons.
     *
     * @param region of type Region
     * @param numberOfEntries number of entries for population
     */
    public static void populateRegionByPersons(Region region, int numberOfEntries) {
	for (int i = 1; i <= numberOfEntries; i++) {
	    region.put(i, new Person(i, Arrays.asList(Integer.toString(i), Integer.toString(i * 2))));
	}
    }
}
