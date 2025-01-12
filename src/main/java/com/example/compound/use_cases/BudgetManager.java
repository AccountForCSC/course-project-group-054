package com.example.compound.use_cases;

import com.example.compound.entities.*;
import com.example.compound.use_cases.gateways.RepositoryGatewayI;
import com.example.compound.use_cases.transfer_data.BudgetTransferData;
import com.example.compound.use_cases.transfer_data.GroupTransferData;
import com.example.compound.use_cases.transfer_data.ItemTransferData;
//import com.example.compound.use_cases.gateways.RepositoryGateway;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BudgetManager {
    private final RepositoryGatewayI<BudgetTransferData> budgetRepositoryGateway;
    private final RepositoryGatewayI<GroupTransferData> groupRepositoryGateway;
    private final RepositoryGatewayI<ItemTransferData> itemRepositoryGateway;
//    private final RepositoryGateway repositoryGateway;

    public BudgetManager(RepositoryGatewayI<BudgetTransferData> budgetRepositoryGateway,
                         RepositoryGatewayI<GroupTransferData> groupRepositoryGateway,
                         RepositoryGatewayI<ItemTransferData> itemRepositoryGateway
//                         RepositoryGateway repositoryGateway
    ) {
        this.budgetRepositoryGateway = budgetRepositoryGateway;
        this.groupRepositoryGateway = groupRepositoryGateway;
        this.itemRepositoryGateway = itemRepositoryGateway;
//        this.repositoryGateway = repositoryGateway;
    }

    /**
     * Create a new Budget with the given name and limit on spending and associate it with the group with the given GUID.
     * @param GUID the UID of the group with which the new Budget is to be associated
     * @param name the name of the budget
     * @param maxSpend the budget's limit on spending
     * @return whether the Budget was added successfully
     */
    public boolean create(String GUID, String name, double maxSpend) {
        GroupTransferData group = this.groupRepositoryGateway.findByUID(GUID);
//        Group group = this.repositoryGateway.findByGUID(GUID);
        if (group == null) {
            return false;
        }

//        String BUID = Integer.toString(this.repositoryGateway.getNewBUID());
        Budget budget = new Budget("", name, maxSpend); // a new BUID will be generated by the repository
        group.addBudget(budget);

//        this.repositoryGateway.addBudget(budget);
//        this.repositoryGateway.updateGroup(group);
        this.budgetRepositoryGateway.save(new BudgetTransferData(budget));
        this.groupRepositoryGateway.save(group);
        return true;
    }

    /**
     * Return the BUID of the budget with the given name.
     * @param name the name of the budget
     * @return the BUID of the budget with the given name, or null if there is no budget with the given name
     */
    public String getBUIDFromName(String name) {
        List<BudgetTransferData> budgets = budgetRepositoryGateway.findAll();
//        List<Budget> budgets = repositoryGateway.getBudgets();
        for (BudgetTransferData budget : budgets) {
            if (budget.getName().equals(name)) {
                return budget.getBUID();
            }
        }
        return null;
    }

    /**
     * Return a list of the names of the items in the budget with the given BUID.
     * @param BUID the UID of the budget
     * @return a list of the names of the items in the budget with the given BUID, or null if there is no budget with
     *         the given BUID
     */
    public List<String> getItems(String BUID) {
        Budget budget = this.budgetRepositoryGateway.findByUID(BUID).toBudget();
//        Budget budget = this.repositoryGateway.findByBUID(BUID);
        if (budget == null) {
            return null;
        } else {
            return new ArrayList<>(budget.getItems().keySet());
        }
    }

    /**
     * Return the IUID of the item with the given name.
     * @param name the name of the item
     * @return the IUID of the item with the given name, or null if there is no item with the given name
     */
    public String getIUIDFromName(String name) {
        List<ItemTransferData> items = itemRepositoryGateway.findAll();
//        List<Item> items = this.repositoryGateway.getItems();
        for (ItemTransferData item : items) {
            if (item.getName().equals(name)) {
                return item.getIUID();
            }
        }
        return null;
    }

    /**
     * Return a list of expenses generated from the items in the budget with the given BUID
     * @param BUID the UID of the budget
     * @param expenseManager the current ExpenseManager instance
     * @return a list of expenses generated from the items in the budget with the given BUID, or null if there is no
     *         budget with the given BUID
     */
    public List<Expense> toExpenses(String BUID, ExpenseManager expenseManager) {
        Budget budget = this.budgetRepositoryGateway.findByUID(BUID).toBudget();
//        Budget budget = this.repositoryGateway.findByBUID(BUID);
        if (budget == null) {
            return null;
        }

        List<Expense> expenses = new ArrayList<>();
        for (String itemName : budget.getItems().keySet()) {
            Item item = budget.getItemByName(itemName);
            Expense expense = expenseManager.createExpense(item);
            expenses.add(expense);
        }
        return expenses;
    }

    /**
     * Add an item with the given name, cost, and quantity to the Budget with the given BUID.
     * @param BUID the BUID of the budget
     * @param name the name of the item
     * @param cost the cost of the item
     * @param quantity the quantity of the item
     * @return the IUID of the new Item, or null if there is no budget with the given BUID
     */
    public String addItem(String BUID, String name, double cost, int quantity) {
        Budget budget = this.budgetRepositoryGateway.findByUID(BUID).toBudget();
//        Budget budget = this.repositoryGateway.findByBUID(BUID);
        if (budget == null) {
            return null;
        }

//        String IUID = Integer.toString(this.repositoryGateway.getNewIUID());
        Item newItem = new Item("", name, cost, quantity); // a new IUID will be generated by the repository
        budget.addItem(newItem);
        String IUID = this.itemRepositoryGateway.save(new ItemTransferData(newItem));
//        String IUID = this.repositoryGateway.addItem(newItem);
        this.budgetRepositoryGateway.save(new BudgetTransferData(budget));
//        this.repositoryGateway.updateBudget(budget);
        return IUID;
    }

    /**
     * Change the quantity of the item with the given UID to the given value
     * @param IUID the UID of the item
     * @param newQuantity the new quantity of the item
     * @return whether the item's quantity was changed
     */
    public boolean changeItemQuantity(String IUID, int newQuantity) {
        List<BudgetTransferData> budgets = this.budgetRepositoryGateway.findAll();
//        List<Budget> budgets = this.repositoryGateway.getBudgets();
        for (BudgetTransferData budgetTransferData : budgets) {
            Budget budget = budgetTransferData.toBudget();
            Item item = budget.getItemByIUID(IUID);
            if (item != null) {
                boolean changed = budget.changeQuantity(IUID, newQuantity);
                this.itemRepositoryGateway.save(new ItemTransferData(item));
//                this.repositoryGateway.updateItem(item);
                return changed;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * Remove the item with the given UID
     * @param IUID the UID of the item
     * @return whether the item was removed
     */
    public boolean removeItem(String IUID) {
        List<BudgetTransferData> budgets = this.budgetRepositoryGateway.findAll();
//        List<Budget> budgets = this.repositoryGateway.getBudgets();
        for (BudgetTransferData budgetTransferData : budgets) {
            Budget budget = budgetTransferData.toBudget();
            Item item = budget.getItemByIUID(IUID);
            if (item != null) {
                budget.removeItem(item.getIUID());
                this.itemRepositoryGateway.deleteById(IUID);
//                this.repositoryGateway.removeItem(item);
                this.budgetRepositoryGateway.save(new BudgetTransferData(budget));
//                this.repositoryGateway.updateBudget(budget);
            }
            return true;
        }
        return false;
    }

    /**
     * Get the spending limit of the budget with the given BUID.
     * @param BUID the UID of the budget
     * @return the spending limit of the budget, or null if there is no budget with the given BUID
     */
    public Double getMaxSpend(String BUID) {
        BudgetTransferData budgetTransferData = this.budgetRepositoryGateway.findByUID(BUID);
//        Budget budget = this.repositoryGateway.findByBUID(BUID);
        if (budgetTransferData == null) {
            return null;
        }

        return budgetTransferData.getMaxSpend();
    }

    /**
     * Ser the spending limit of the budget with the given BUID to the given value.
     * @param BUID the UID of the budget
     * @param newMaxSpend the new spending limit
     * @return whether the spending limit was changed. In particular, this method returns false if there is no budget
     *         with the given BUID.
     */
    public boolean setMaxSpend(String BUID, double newMaxSpend) {
        Budget budget = this.budgetRepositoryGateway.findByUID(BUID).toBudget();
//        Budget budget = this.repositoryGateway.findByBUID(BUID);
        if (budget == null) {
            return false;
        }

        budget.setMaxSpend(newMaxSpend);
        this.budgetRepositoryGateway.save(new BudgetTransferData(budget));
//        this.repositoryGateway.updateBudget(budget);
        return true;
    }

    /**
     * Remove the budget with the given BUID from the group with the given GUID.
     * @param GUID the GUID of the group
     * @param BUID the BUID of the budget
     * @return whether the budget was removed successfully. In particular, this method returns false if there is no
     *         group with the given GUID or if such a group exists but there is no budget with the given BUID in that
     *         group.
     */
    public boolean remove(String GUID, String BUID) {
        GroupTransferData group = this.groupRepositoryGateway.findByUID(GUID);
//        Group group = this.repositoryGateway.findByGUID(GUID);
        if (group == null) {
            return false;
        }

        boolean removed = group.removeBudget(BUID);
        if (!removed) {
            return false;
        }
        this.budgetRepositoryGateway.deleteById(BUID); // TODO: What if BUID is invalid?
//        this.repositoryGateway.removeBudget(BUID);
        this.groupRepositoryGateway.save(group);
//        this.repositoryGateway.updateGroup(group);
        return true;
    }

    /**
     * For each expense in the list of expenses generated from the items in the budget with the given BUID, add that
     * expense to the group with the given GUID.
     * @param GUID the UID of the group
     * @param BUID the UID of the budget
     * @param expenseManager the current ExpenseManager instance
     * @return whether the expenses generated from the items in the budget with the given UID were added to the group
     *         with the given UID. In particular, this method returns false if there is no budget with the given BUID
     *         or no group with the given GUID.
     */
    public boolean addExpensesToGroup(String GUID, String BUID, ExpenseManager expenseManager) {
//        Group group = this.repositoryGateway.findByGUID(GUID);
        GroupTransferData group = this.groupRepositoryGateway.findByUID(GUID);
        if (group == null) {
            return false;
        }

        List<Expense> budgetExpenses;
        try {
            budgetExpenses = Objects.requireNonNull(toExpenses(BUID, expenseManager));
        } catch (NullPointerException e) {
            return false;
        }
        for (Expense expense : budgetExpenses) {
            group.addExpense(expense);
        }
        this.groupRepositoryGateway.save(group);
//        this.repositoryGateway.updateGroup(group);
        return true;
    }

    /**
     * Return a list of the names of the budgets associated with the group with the given GUID.
     * @param GUID the GUID of the group
     * @return a list of the names of the budgets associated with the group with the given GUID, or null if there is no
     *         group with the given GUID
     */
    public List<String> getBudgetNameList(String GUID) {
        GroupTransferData group = this.groupRepositoryGateway.findByUID(GUID);
//        Group group = this.repositoryGateway.findByGUID(GUID);
        if (group == null) {
            return null;
        }

        List<Budget> budgets = group.getBudgets();
        List<String> budgetNames = new ArrayList<>();
        for (Budget budget : budgets) {
            budgetNames.add(budget.getName());
        }
        return budgetNames;
    }
}
