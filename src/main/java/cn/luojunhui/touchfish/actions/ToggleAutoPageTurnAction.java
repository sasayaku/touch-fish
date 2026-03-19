package cn.luojunhui.touchfish.actions;

import cn.luojunhui.touchfish.config.BookSettingsState;
import cn.luojunhui.touchfish.windwos.Book;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

/**
 * 切换自动翻页开关，默认快捷键 Ctrl+Caps Lock。
 */
public class ToggleAutoPageTurnAction extends AnAction implements DumbAware {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // getState() 返回单例自身，勿再 loadState(自己)：XmlSerializerUtil.copyBean(this,this) 易导致状态异常
        BookSettingsState settings = BookSettingsState.getInstance();
        settings.setAutoPageTurnEnabled(!settings.getAutoPageTurnEnabled());
        Book.applyAutoPageTurnState();
    }
}
