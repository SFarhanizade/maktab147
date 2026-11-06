package ir.maktabsharif.files.todo.ui;

import ir.maktabsharif.files.todo.dto.TaskView;
import ir.maktabsharif.files.todo.entity.TaskStatus;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TaskTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {"Title", "Category", "Due", "Status", "ID"};
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final List<TaskView> all = new ArrayList<>();
    private final List<TaskView> visible = new ArrayList<>();

    private String filter = "";
    private TaskStatus statusFilter = null;
    private Integer categoryFilter = null;

    public void setData(List<TaskView> tasks) {
        all.clear();
        all.addAll(tasks);
        applyFilter();
    }

    public void setFilter(String text) {
        this.filter = text == null ? "" : text.toLowerCase(Locale.ROOT).trim();
        applyFilter();
    }

    public void setStatusFilter(TaskStatus status) {
        this.statusFilter = status;
        applyFilter();
    }

    public void setCategoryFilter(Integer categoryId) {
        this.categoryFilter = categoryId;
        applyFilter();
    }

    public TaskView taskAt(int modelRow) {
        return visible.get(modelRow);
    }

    public boolean isOverdue(int modelRow) {
        TaskView t = visible.get(modelRow);
        return t.status() == TaskStatus.ACTIVE && t.dueDate() != null && t.dueDate().isBefore(LocalDateTime.now());
    }

    private void applyFilter() {
        visible.clear();
        for (TaskView t : all) {
            //fixme: filter is not applied somehow
            if (statusFilter != null && t.status() != statusFilter) continue;
            if (categoryFilter != null && t.categoryId() != categoryFilter) continue;
            if (!filter.isEmpty()) {
                String haystack = t.title().toLowerCase(Locale.ROOT);
                if (!haystack.contains(filter)) continue;
            }
            visible.add(t);
        }
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return visible.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int c) {
        return COLUMNS[c];
    }

    @Override
    public boolean isCellEditable(int r, int c) {
        return false;
    }

    @Override
    public Object getValueAt(int row, int col) {
        TaskView t = visible.get(row);
        return switch (col) {
            case 0 -> t.title();
            case 1 -> t.categoryName();
            case 2 -> t.dueDate() == null ? "" : t.dueDate().format(FMT);
            case 3 -> t.status();
            case 4 -> t.id();
            default -> "";
        };
    }
}