package net.megx.megdb;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

//The order is important here for succesfult test results
@RunWith(Suite.class)
@SuiteClasses({ PubMapArticleDeleteTest.class,PubMapArticleInsertNewTest.class })
public class AllTestsPubMap {

}