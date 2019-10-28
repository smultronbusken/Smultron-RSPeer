package org.smultron.framework.content.banking;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.Definitions;
import org.rspeer.runetek.api.component.ItemTables;
import org.rspeer.runetek.api.query.ItemQueryBuilder;
import org.rspeer.runetek.event.listeners.BankLoadListener;
import org.rspeer.runetek.event.listeners.ItemTableListener;
import org.rspeer.runetek.event.types.ItemTableEvent;
import org.rspeer.runetek.event.types.BankLoadEvent;
import org.rspeer.runetek.providers.RSItemTable;
import org.rspeer.runetek.providers.RSNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Thanks to http://forums.rspeer.org/topic/1237/bank-cache
 */
public class BankCache  implements ItemTableListener, BankLoadListener
{
    private static RSItemTable bankTable = ItemTables.lookup(ItemTables.BANK);

    private static BankCache instance;

    private BankCache() {

    }

    public static BankCache getInstance() {
        if (instance == null)
            instance = new BankCache();
        return instance;
    }

    public boolean mustCheckBank() {
        return bankTable == null;
    }

    public boolean contains(int... idsToFind) {
        return bankTable.contains(idsToFind);
    }

    public boolean containsAll(int... idsToFind) {
        return bankTable.containsAll(idsToFind);
    }

    public int getCount(boolean includeStacks, int... idsToFind) {
        return bankTable.getCount(includeStacks, idsToFind);
    }

    public int getCoins() {
        return bankTable.getCount(true, 617, 8890, 6964, 995) + ItemTables.lookup(ItemTables.INVENTORY).getCount(true, 617, 8890, 6964, 995);
    }

    public boolean contains(String... names) {
        return Arrays
		.stream(bankTable.getIds())
		.anyMatch(id -> Arrays.stream(names)
		.anyMatch(name -> Definitions.getItem(id) != null &&
          	Definitions.getItem(id).getName().equals(name)));
    }

    public boolean containsAll(String... names) {
        return Arrays
                .stream(names)
                .allMatch(name -> bankTable.contains(Definitions.getItem(name, a -> true).getId()));
    }

    public int getCount(boolean includeStacks, String... names) {
        return Arrays
                .stream(names)
                .map(name -> bankTable.getCount(includeStacks, Definitions.getItem(name, a -> true).getId()))
                .reduce(0, Integer::sum);
    }

    public ItemQueryBuilder newQuery() {
        List<Item> items = new ArrayList<>();

        for (int i = 0; i < bankTable.getIds().length; i++) {
            items.add(new Item(new InterfaceComponent(null), 0, bankTable.getIds()[i], bankTable.getStackSizes()[i]));
        }

        return new ItemQueryBuilder(() -> items);
    }

    public void setToEmpty() {
        bankTable = new RSItemTable()
        {
            @Override public int[] getStackSizes() {
                return new int[0];
            }

            @Override public int[] getIds() {
                return new int[0];
            }

            @Override public RSNode getNext() {
                return null;
            }

            @Override public long getKey() {
                return 0;
            }

            @Override public RSNode getPrevious() {
                return null;
            }
        };
    }

    public void notify(ItemTableEvent itemTableEvent) {
        if (itemTableEvent.getTableKey() == ItemTables.BANK)
            bankTable = ItemTables.lookup(ItemTables.BANK);
    }

    public void notify(BankLoadEvent bankLoadedEvent) {
        bankTable = bankLoadedEvent.getBankItemTable();
    }
}
