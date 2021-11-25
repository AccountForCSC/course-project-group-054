package com.example.compound.use_cases;

import com.example.compound.entities.*;
import com.example.compound.use_cases.gateways.RepositoryGateway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BudgetManager {
//    private final BudgetRepositoryGateway budgetRepositoryGateway;
//    private final GroupRepositoryGateway groupRepositoryGateway;
//    private final ItemRepositoryGateway itemRepositoryGateway;
    private final RepositoryGateway repositoryGateway;

    public BudgetManager(//BudgetRepositoryGateway budgetRepositoryGateway,
//                         GroupRepositoryGateway groupRepositoryGateway,
//                         ItemRepositoryGateway itemRepositoryGateway,
                         RepositoryGateway repositoryGateway
    ) {
//        this.budgetRepositoryGateway = budgetRepositoryGateway;
//        this.groupRepositoryGateway = groupRepositoryGateway;
//        this.itemRepositoryGateway = itemRepositoryGateway;
        this.repositoryGateway = repositoryGateway;
    }

    /**
     * Create a new Budget with the given name and limit on spending and associate it with the group with the given GUID.
     * @param GUID the UID of the group with which the new Budget is to be associated
     * @param name the name of the budget
     * @param maxSpend the budget's limit on spending
     * @return whether the Budget was added successfully
     */
    public boolean create(String GUID, String name, double maxSpend) {
//        Group group = this.groupRepositoryGateway.findById(GUID); // TODO: instead of GUID, maybe group name? using findAll() and then loop over to get that group
        Group group = this.repositoryGateway.findByGUID(GUID);

        if (group == null) {
            return false;
        }
//        String BUID = Integer.toString(this.repositoryGateway.getNewBUID());
        Budget budget = new Budget("", name, maxSpend); // a new BUID will be generated by the repository
        group.addBudget(budget);

        this.repositoryGateway.addBudget(budget);
        this.repositoryGateway.updateGroup(group);
//        this.budgetRepositoryGateway.save(budget);
//        this.groupRepositoryGateway.save(group);
        return true;
    }

    /**
     * Return the BUID of the budget with the given name.
     * @param name the name of the budget
     * @return the BUID of the budget with the given name
     */
    public String getBUIDFromName(String name) {
//        List<Budget> budgets = budgetRepositoryGateway.findAll();
        List<Budget> budgets = repositoryGateway.getBudgets();
        for (Budget budget : budgets) {
            if (budget.getName().equals(name)) {
                return budget.getBUID();
            }
        }
        return null;
    }

    /**
     * Return a list of the names of the items in the budget with the given BUID.
     * @param BUID the UID of the budget
     * @return a list of the names of the items in the budget with the given BUID
     */
    public List<String> getItems(String BUID) {
//        return new ArrayList<>(this.budgetRepositoryGateway.findById(BUID).getItems().keySet());
        return new ArrayList<>(this.repositoryGateway.findByBUID(BUID).getItems().keySet());
    }

    /**
     * Return the IUID of the item with the given name.
     * @param name the name of the item
     * @return the IUID of the item with the given name
     */
    public String getIUIDFromName(String name) {
//        List<Item> items = itemRepositoryGateway.findAll();
        List<Item> items = this.repositoryGateway.getItems();
        for (Item item : items) {
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
     * @return a list of expenses generated from the items in the budget with the given BUID
     */
    public List<Expense> toExpenses(String BUID, ExpenseManager expenseManager) {
//        Budget budget = this.budgetRepositoryGateway.findById(BUID);
        Budget budget = this.repositoryGateway.findByBUID(BUID);
        if (budget == null) {
            return null;
        }

        List<Expense> expenses = new ArrayList<>();
        for (String itemName : budget.getItems().keySet()) {
            Item item = budget.getItem(itemName);
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
     * @return the IUID of the new Item
     */
    public String addItem(String BUID, String name, double cost, int quantity) { // TODO: Instead of passing in a Budget, maybe pass in just the BUID/name instead?
//        Budget budget = this.budgetRepositoryGateway.findById(BUID);
        Budget budget = this.repositoryGateway.findByBUID(BUID);
        if (budget == null) {
            return null;
        }
//        String IUID = Integer.toString(this.repositoryGateway.getNewIUID());
        Item newItem = new Item("", name, cost, quantity); // a new IUID will be generated by the repository
        budget.addItem(newItem);
//        this.repositoryGateway.addItem(newItem);
//        String IUID = this.itemRepositoryGateway.save(newItem); // TODO: Is a separate item repository needed?
        String IUID = this.repositoryGateway.addItem(newItem);
//        this.budgetRepositoryGateway.save(budget);
        this.repositoryGateway.updateBudget(budget);
        return IUID;
    }

    /**
     * Change the quantity of the item with the given UID to the given value
     * @param IUID the UID of the item
     * @param newQuantity the new quantity of the item
     * @return whether the item's quantity was changed
     */
    public boolean changeItemQuantity(String IUID, int newQuantity) {
//        List<Budget> budgets = this.budgetRepositoryGateway.findAll();
        List<Budget> budgets = this.repositoryGateway.getBudgets();
        for (Budget budget : budgets) {
            Item item = budget.getItem(IUID); // TODO: This uses the getItem(String name) method
            if (item != null) {
                item.setQuantity(newQuantity);
//                this.itemRepositoryGateway.save(item);
                this.repositoryGateway.updateItem(item);
            }
            return true;
        }
        return false;
    }

    /**
     * Remove the item with the given UID
     * @param IUID the UID of the item
     * @return whether the item was removed
     */
    public boolean removeItem(String IUID) {
//        List<Budget> budgets = this.budgetRepositoryGateway.findAll();
        List<Budget> budgets = this.repositoryGateway.getBudgets();
        for (Budget budget : budgets) {
            Item item = budget.getItem(IUID);
            if (item != null) {
                budget.removeItem(item.getName());
//                this.itemRepositoryGateway.deleteById(IUID);
                this.repositoryGateway.removeItem(item);
//                this.budgetRepositoryGateway.save(budget);
                this.repositoryGateway.updateBudget(budget);
            }
            return true;
        }
        return false;
    }

    /**
     * Get the spending limit of the budget with the given BUID.
     * @param BUID the UID of the budget
     * @return the spending limit of the budget
     */
    public double getMaxSpend(String BUID) {
//        Budget budget = this.budgetRepositoryGateway.findById(BUID);
        Budget budget = this.repositoryGateway.findByBUID(BUID);
        return budget.getMaxSpend();
    }

    /**
     * Ser the spending limit of the budget with the given BUID to the given value.
     * @param BUID the UID of the budget
     * @param newMaxSpend the new spending limit
     */
    public void setMaxSpend(String BUID, double newMaxSpend) {
//        Budget budget = this.budgetRepositoryGateway.findById(BUID);
        Budget budget = this.repositoryGateway.findByBUID(BUID);
        budget.setMaxSpend(newMaxSpend);
//        this.budgetRepositoryGateway.save(budget);
        this.repositoryGateway.updateBudget(budget);
    }

    /**
     * Return a mapping from item name to the cost of the item as a percentage of the total cost of all items in the
     * budget with the given UID.
     * @param BUID the UID of the budget
     * @return a mapping from item name to the cost of the item as a percentage of the total cost of all items in the
     *         budget, or null if the Budget's getTotalCost returns 0
     */
    public HashMap<String, Double> getPercentages(String BUID) {
//        Budget budget = this.budgetRepositoryGateway.findById(BUID);
        Budget budget = this.repositoryGateway.findByBUID(BUID);
        return budget.getPercentages();
    }

    /**
     * Remove the budget with the given BUID from the group with the given GUID.
     * @param GUID the GUID of the group
     * @param BUID the BUID of the budget
     * @return whether the budget was removed successfully
     */
    public boolean remove(String GUID, String BUID) { // TODO: Instead of passing in the GUID, maybe pass in the name instead?
//        Group group = this.groupRepositoryGateway.findById(GUID); // TODO: What if GUID is invalid?
        Group group = this.repositoryGateway.findByGUID(GUID);
        boolean removed = group.removeBudget(BUID);
        if (!removed) {
            return false;
        }
//        this.budgetRepositoryGateway.deleteById(BUID); // TODO: What if BUID is invalid?
        this.repositoryGateway.removeBudget(BUID);
//        this.groupRepositoryGateway.save(group);
        this.repositoryGateway.updateGroup(group);
        return true;
    }

    /**
     * For each expense in the list of expenses generated from the items in the budget with the given BUID, add that
     * expense to the group with the given GUID.
     * @param GUID the UID of the group
     * @param BUID the UID of the budget
     * @param expenseManager the current ExpenseManager instance
     */
    public void addExpensesToGroup(String GUID, String BUID, ExpenseManager expenseManager) {
        Group group = this.repositoryGateway.findByGUID(GUID);
        List<Expense> budgetExpenses = toExpenses(BUID, expenseManager);
        for (Expense expense : budgetExpenses) {
            group.addExpense(expense);
        }
//        this.groupRepositoryGateway.save(group);
        this.repositoryGateway.updateGroup(group);
    }

    /**
     * Return a list of the names of the budgets associated with the group with the given GUID.
     * @param GUID the GUID of the group
     * @return a list of the names of the budgets associated with the group with the given GUID
     */
    public List<String> getBudgetNameList(String GUID) {
//        Group group = this.groupRepositoryGateway.findById(GUID);
        Group group = this.repositoryGateway.findByGUID(GUID);
        System.out.println("g"+group);
        List<Budget> budgets = group.getBudgets();
        List<String> budgetNames = new ArrayList<>();
        for (Budget budget : budgets) {
            budgetNames.add(budget.getName());
        }
        return budgetNames;
    }
}