import dev.fudgeu.pquery.plugin.Plugin
import dev.fudgeu.pquery.plugin.PluginBuilder
import kotlin.test.DefaultAsserter.assertEquals
import kotlin.test.Test
import kotlin.test.fail

class PluginBuilderTest {

    @Test
    fun testFromJSONBasic() {
        val basicTestJson = """
            {
                "name": "TestPlugin",
                "variables": {
                    "foo.bar": 5,
                    "bar": -20,
                    "bar.foo": "hello",
                    "foo": true,
                    "TestPlugin.blerp.blarp": false
                }
            }
        """.trimIndent()

        var plugin: Plugin? = null
        try {
            plugin = PluginBuilder.fromJson(basicTestJson).build()
        } catch (e: Throwable) {
            fail("Failed with: ${e.message}")
        }

        assertEquals("Name does not match", plugin.name, "TestPlugin")
        assertEquals("foo does not match", true, plugin.getBooleanVar("TestPlugin.foo")?.resolve())
        assertEquals("foo.bar does not match", 5.0, plugin.getNumberVar("TestPlugin.foo.bar")?.resolve())
        assertEquals("bar does not match", -20.0, plugin.getNumberVar("TestPlugin.bar")?.resolve())
        assertEquals("bar.foo does not match", "hello", plugin.getStringVar("TestPlugin.bar.foo")?.resolve())
        assertEquals("blerp.blarp does not match", false, plugin.getBooleanVar("TestPlugin.blerp.blarp")?.resolve())
    }

    @Test
    fun testFromJSONTreeStructure() {
        val treeTestJson = """
            {
                "name": "TestPlugin",
                "variables": {
                    "foo": {
                      ".": true,
                      "bar": 5
                    },
                    "bar": {
                      ".": -20,
                      "foo": "hello"
                    },
                    "blerp": {
                      "blarp": false
                    }
                }
            }
        """.trimIndent()

        var plugin: Plugin? = null
        try {
            plugin = PluginBuilder.fromJson(treeTestJson).build()
        } catch (e: Throwable) {
            fail("Failed with: ${e.message}")
        }

        assertEquals("Name does not match", plugin.name, "TestPlugin")
        assertEquals("foo does not match", true, plugin.getBooleanVar("TestPlugin.foo")?.resolve())
        assertEquals("foo.bar does not match", 5.0, plugin.getNumberVar("TestPlugin.foo.bar")?.resolve())
        assertEquals("bar does not match", -20.0, plugin.getNumberVar("TestPlugin.bar")?.resolve())
        assertEquals("bar.foo does not match", "hello", plugin.getStringVar("TestPlugin.bar.foo")?.resolve())
        assertEquals("blerp.blarp does not match", false, plugin.getBooleanVar("TestPlugin.blerp.blarp")?.resolve())
    }

    @Test
    fun testFromJSONMixed() {
        val mixedTestJson = """
            {
                "name": "TestPlugin",
                "variables": {
                    "foo": {
                      ".": true,
                      "bar": 5
                    },
                    "bar": {
                      ".": -20,
                      "foo": "hello",
                      "foo.yellow": "test1"
                    },
                    "blerp": {
                      "blarp": false
                    },
                    "blerp.blarp.blorp": 3,
                    "foo.zar": {
                      ".": true,
                      "a": "b",
                      "c.d": "e"
                    }
                }
            }
        """.trimIndent()

        var plugin: Plugin? = null
        try {
            plugin = PluginBuilder.fromJson(mixedTestJson).build()
        } catch (e: Throwable) {
            fail("Failed with: ${e.message}")
        }

        assertEquals("Name does not match", plugin.name, "TestPlugin")
        assertEquals("foo does not match", true, plugin.getBooleanVar("TestPlugin.foo")?.resolve())
        assertEquals("foo.bar does not match", 5.0, plugin.getNumberVar("TestPlugin.foo.bar")?.resolve())
        assertEquals("foo.zar does not match", true, plugin.getBooleanVar("TestPlugin.foo.zar")?.resolve())
        assertEquals("foo.zar.a does not match", "b", plugin.getStringVar("TestPlugin.foo.zar.a")?.resolve())
        assertEquals("foo.zar.c.d does not match", "e", plugin.getStringVar("TestPlugin.foo.zar.c.d")?.resolve())
        assertEquals("bar does not match", -20.0, plugin.getNumberVar("TestPlugin.bar")?.resolve())
        assertEquals("bar.foo does not match", "hello", plugin.getStringVar("TestPlugin.bar.foo")?.resolve())
        assertEquals("bar.foo.yellow does not match", "test1", plugin.getStringVar("TestPlugin.bar.foo.yellow")?.resolve())
        assertEquals("blerp.blarp does not match", false, plugin.getBooleanVar("TestPlugin.blerp.blarp")?.resolve())
        assertEquals("blerp.blarp.blorp does not match", 3.0, plugin.getNumberVar("TestPlugin.blerp.blarp.blorp")?.resolve())
    }
}