package com.example.orgcore.internal.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.example.orgcore.internal.internal.datastore.StringDataStoreProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class StringDataStoreProviderTest {


    private class InMemoryDataStore : DataStore<Preferences> {
        private val _data = MutableStateFlow(emptyPreferences())
        override val data: Flow<Preferences> = _data
        override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
            val newData = transform(_data.value)
            _data.value = newData
            return newData
        }
    }

    private lateinit var stringDataStoreProvider: StringDataStoreProvider

    @Before
    fun setup(){
        val dataStore = InMemoryDataStore()
        stringDataStoreProvider = StringDataStoreProvider(dataStore)
    }

    @Test
    fun when_put_some_value_expect_to_find_it_in_the_observe_and_contain_return_true()= runTest {
        stringDataStoreProvider.put("key","string")
        launch {
            assert(stringDataStoreProvider.observe("key","").first() == "string")
        }
        assert(stringDataStoreProvider.contains("key"))
    }

    @Test
    fun when_observing_non_existKey_expect_to_return_default_value_and_contain_return_false()= runTest {
        launch {
            assert(stringDataStoreProvider.observe("key2","").first() == "")
        }
        assert(!stringDataStoreProvider.contains("key2"))
    }

    @Test
    fun when_removing_existing_key_expect_to_return_it_and_it_wont_remain_in_string_datastore()=
        runTest {
            //add on key to make sure it is existing in the datastore before removing it
            //to fulfill all the test case pres condition
            stringDataStoreProvider.put("key1","string1")

            val removedValue = stringDataStoreProvider.remove("key1")
            assert(removedValue == "string1")
            assert(!stringDataStoreProvider.contains("key1"))
        }

    @Test
    fun when_calling_after_clear_getAll_expect_to_return_nothing()= runTest {
        //To fulfill the test use case pre_condition add some values
        stringDataStoreProvider.put("key3","string3")
        stringDataStoreProvider.clear()
        launch {
            stringDataStoreProvider.getAll().first().also {
                assert(it.isEmpty())
            }
        }
    }

    @Test
    fun when_calling_getAll_expect_to_get_all_the_existing_item() = runTest {
        //To fulfill the test use case pre_condition clear the data store
        stringDataStoreProvider.clear()
        //To fulfill the test use case pre_condition add some values
        stringDataStoreProvider.put("key4","string4")
        stringDataStoreProvider.put("key5","string5")
        launch {
            stringDataStoreProvider.getAll().first().also {
                assert(it.size  == 2)
                assert(it.contains("key4"))
                assert(it["key4"] == "string4")
                assert(it.containsKey("key5"))
                assert(it["key5"] == "string5")
                assert(!it.contains("key3"))
            }
        }
    }
}