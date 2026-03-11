package cn.luojunhui.touchfish.config;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;

import javax.swing.*;

/**
 * 配置页的表单
 */
public class BookSettingsComponent {

    private final JPanel settingPanel;
    private final TextFieldWithBrowseButton chooseFileBtn = new TextFieldWithBrowseButton();
    private JTextField pageTextField = new JTextField();
    private JTextField pageSizeTextField = new JTextField();
    private JCheckBox autoPageTurnCheckBox = new JCheckBox("开启自动翻页");
    private JTextField autoPageTurnSecondsField = new JTextField("5", 4);

    private static final String AUTO_PAGE_TURN_HINT = "自动翻页使用说明：\n"
            + "• 勾选「开启自动翻页」并设置间隔秒数，打开阅读窗口后将按间隔自动翻页。\n"
            + "• 默认快捷键 Ctrl+Caps Lock 可随时开关自动翻页。\n"
            + "• 修改快捷键：打开 设置 → Keymap，搜索「切换自动翻页」后修改或绑定快捷键。";

    public BookSettingsComponent() {
        JTextArea hintArea = new JTextArea(AUTO_PAGE_TURN_HINT);
        hintArea.setEditable(false);
        hintArea.setLineWrap(true);
        hintArea.setWrapStyleWord(true);
        hintArea.setBackground(UIManager.getColor("Panel.background"));
        hintArea.setBorder(JBUI.Borders.empty(4, 0));
        hintArea.setFont(UIManager.getFont("Label.font"));

        settingPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("文件路径: "), chooseFileBtn, 1, false)
                .addLabeledComponent(new JBLabel("每页行数: "), pageSizeTextField, 1, false)
                .addLabeledComponent(new JBLabel("当前页码: "), pageTextField, 1, false)
                .addSeparator()
                .addLabeledComponent(new JBLabel("自动翻页: "), autoPageTurnCheckBox, 1, false)
                .addLabeledComponent(new JBLabel("翻页间隔(秒): "), autoPageTurnSecondsField, 1, false)
                .addLabeledComponent(new JBLabel(""), hintArea, 1, false)
                .getPanel();
    }

    /**
     * 表单初始化赋值
     */
    public void init() {
        //按钮绑定事件
        this.chooseFileBtn.addBrowseFolderListener("选择文件", null, null,
                FileChooserDescriptorFactory.createSingleFileDescriptor("txt"));

        BookSettingsState settings = BookSettingsState.getInstance().getState();
        String bookPath = settings.getBookPath();
        if (StringUtil.isNotEmpty(bookPath)) {
            this.setBookPath(bookPath);
            this.setPage(settings.getPage());
            this.setPageSize(settings.getPageSize());
        }
        this.setAutoPageTurnEnabled(settings.getAutoPageTurnEnabled());
        this.setAutoPageTurnSeconds(settings.getAutoPageTurnSeconds());
    }

    public JPanel getPanel() {
        return settingPanel;
    }

    public String getBookPath() {
        return chooseFileBtn.getTextField().getText();
    }

    public void setBookPath(String s) {
        this.chooseFileBtn.getTextField().setText(s);
    }

    /**
     * 获取设置的行数,不能小于1
     *
     * @return
     */
    public int getPage() {
        int line = Integer.valueOf(this.pageTextField.getText());
        if (line < 1) {
            this.pageTextField.setText("1");
            return 1;
        }
        return line;
    }

    public void setPage(int line) {
        this.pageTextField.setText(String.valueOf(line));
    }

    /**
     * 获取加载行数,不能小于1
     *
     * @return
     */
    public int getPageSize() {
        int rowCount = Integer.valueOf(this.pageSizeTextField.getText());
        if (rowCount < 1) {
            this.setPageSize(1);
            return 1;
        }
        return rowCount;
    }

    public void setPageSize(int rowCount) {
        this.pageSizeTextField.setText(String.valueOf(rowCount));
    }

    public boolean isAutoPageTurnEnabled() {
        return autoPageTurnCheckBox.isSelected();
    }

    public void setAutoPageTurnEnabled(boolean enabled) {
        this.autoPageTurnCheckBox.setSelected(enabled);
    }

    public int getAutoPageTurnSeconds() {
        try {
            int sec = Integer.parseInt(autoPageTurnSecondsField.getText().trim());
            return sec < 1 ? 1 : sec;
        } catch (NumberFormatException e) {
            return 5;
        }
    }

    public void setAutoPageTurnSeconds(int seconds) {
        this.autoPageTurnSecondsField.setText(String.valueOf(seconds));
    }

}
