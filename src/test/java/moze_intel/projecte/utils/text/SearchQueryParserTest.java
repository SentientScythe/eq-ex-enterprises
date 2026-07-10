package moze_intel.projecte.utils.text;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

@DisplayName("Test SearchQueryParser")
class SearchQueryParserTest {

    @Test
    @DisplayName("Test basic parsing")
    void testParsing() {
        SearchQueryParser.ISearchQuery query = SearchQueryParser.parse("test");
        Assertions.assertFalse(query.isInvalid());
        
        SearchQueryParser.ISearchQuery invalid = SearchQueryParser.parse("");
        Assertions.assertTrue(invalid.isInvalid());
    }

    @Test
    @DisplayName("Test Quotes and Parentheses")
    void testAdvancedParsing() {
        SearchQueryParser.ISearchQuery query = SearchQueryParser.parse("\"test string\" | 'other'");
        Assertions.assertFalse(query.isInvalid());
        
        SearchQueryParser.ISearchQuery query2 = SearchQueryParser.parse("@(mod1 | mod2)");
        Assertions.assertFalse(query2.isInvalid());
    }

    @Test
    @DisplayName("Test Query Types Matching")
    void testMatching() {
        ItemStack mockStack = Mockito.mock(ItemStack.class);
        Item mockItem = Mockito.mock(Item.class);
        Mockito.when(mockStack.getItem()).thenReturn(mockItem);
        Mockito.when(mockItem.getCreatorModId(mockStack)).thenReturn("projecte");
        
        Component mockComponent = Mockito.mock(Component.class);
        Mockito.when(mockComponent.getString()).thenReturn("Test Item");
        Mockito.when(mockStack.getHoverName()).thenReturn(mockComponent);

        SearchQueryParser.ISearchQuery queryName = SearchQueryParser.parse("test");
        Assertions.assertTrue(queryName.test(null, null, mockStack));

        SearchQueryParser.ISearchQuery queryMod = SearchQueryParser.parse("@projecte");
        Assertions.assertTrue(queryMod.test(null, null, mockStack));
        
        SearchQueryParser.ISearchQuery queryModFail = SearchQueryParser.parse("@minecraft");
        Assertions.assertFalse(queryModFail.test(null, null, mockStack));
    }
}
