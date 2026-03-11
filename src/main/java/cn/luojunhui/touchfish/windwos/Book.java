package cn.luojunhui.touchfish.windwos;

import cn.luojunhui.touchfish.config.BookSettingsState;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Book.class
 *
 * @author junhui
 */
public class Book {
    private static final Logger LOGGER = Logger.getInstance(Book.class);

    private static final int PREV = 0;
    private static final int NEXT = 1;
    private static final int CURRENT = 2;

    private JPanel book;
    private JTextPane text;

    /** 当前显示的 Book 实例，供快捷键回调更新自动翻页状态 */
    static volatile Book currentInstance;
    private Timer autoPageTurnTimer;

    public Book(ToolWindow toolWindow) {
        this.init();
    }


    private void init() {
        BookSettingsState settings = BookSettingsState.getInstance().getState();
        if (settings == null) {
            String info = "请先到插件面板设置阅读信息。";
            LOGGER.info(info);
            Notifications.Bus.notify(new Notification("", "tip", info, NotificationType.INFORMATION));
            return;
        }
        if (StringUtil.isNotEmpty(settings.getBookPath())) {
            this.readText(CURRENT);
        } else {
            this.setText("没有文本文件路径...");
        }

        // 设置键盘监听
        this.text.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                boolean isNext = keyEvent.getKeyCode() == KeyEvent.VK_DOWN;
                boolean isPrev = keyEvent.getKeyCode() == KeyEvent.VK_UP;
                if (isNext) {
                    readText(NEXT);
                } else if (isPrev) {
                    readText(PREV);
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
            }
        });

        // 滚轮翻页：向下滚动下一页，向上滚动上一页
        this.text.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int rotation = e.getWheelRotation();
                if (rotation > 0) {
                    readText(NEXT);
                } else if (rotation < 0) {
                    readText(PREV);
                }
            }
        });

        currentInstance = this;
        updateAutoPageTurnTimer();
    }

    /**
     * 根据配置更新自动翻页定时器：若开启则按间隔翻页，若关闭则停止。
     */
    void updateAutoPageTurnTimer() {
        stopAutoPageTurnTimer();
        BookSettingsState settings = BookSettingsState.getInstance().getState();
        if (settings == null || !settings.getAutoPageTurnEnabled()) {
            return;
        }
        int seconds = settings.getAutoPageTurnSeconds();
        if (seconds < 1) {
            seconds = 1;
        }
        int delayMs = seconds * 1000;
        autoPageTurnTimer = new Timer(delayMs, e -> {
            BookSettingsState s = BookSettingsState.getInstance().getState();
            if (s == null || !s.getAutoPageTurnEnabled()) {
                stopAutoPageTurnTimer();
                return;
            }
            readText(NEXT);
        });
        autoPageTurnTimer.setRepeats(true);
        autoPageTurnTimer.start();
    }

    private void stopAutoPageTurnTimer() {
        if (autoPageTurnTimer != null) {
            autoPageTurnTimer.stop();
            autoPageTurnTimer = null;
        }
    }

    /**
     * 由快捷键动作调用，使当前 Book 实例根据最新配置更新自动翻页定时器。
     */
    public static void applyAutoPageTurnState() {
        Book instance = currentInstance;
        if (instance != null) {
            instance.updateAutoPageTurnTimer();
        }
    }

    /**
     * 按页读取内容
     *
     * @param op user option
     */
    private void readText(int op) {
        BookSettingsState settings = BookSettingsState.getInstance().getState();
        if (settings == null) {
            String info = "请先到插件面板设置阅读信息。";
            LOGGER.info(info);
            Notifications.Bus.notify(new Notification("", "tip", info, NotificationType.INFORMATION));
            return;
        }
        int curPage = settings.getPage();
        List list = null;
        switch (op) {
            case PREV:
                int prevPage = curPage == 1 ? curPage : curPage - 1;
                list = this.readFromPage(settings.getLines(), prevPage, settings.getPageSize());
                if (prevPage == curPage) {
                    Notifications.Bus.notify(new Notification("", "tip", "不能再往前翻页了...", NotificationType.INFORMATION));
                    settings.setPage(1);
                } else {
                    settings.setPage(prevPage);
                }
                break;
            case NEXT:
                int nextPage = curPage == settings.getTotalPage() ? curPage : curPage + 1;
                list = this.readFromPage(settings.getLines(), nextPage, settings.getPageSize());
                if (nextPage == curPage) {
                    Notifications.Bus.notify(new Notification("", "tip", "不能再后前翻页了...", NotificationType.INFORMATION));
                    settings.setPage(curPage);
                } else {
                    settings.setPage(nextPage);
                }
                break;
            case CURRENT:
                list = this.readFromPage(settings.getLines(), curPage, settings.getPageSize());
                break;
            default:
                break;
        }
        this.setText(list);
        //更新值
        BookSettingsState.getInstance().loadState(settings);
    }

    /**
     * 使用java8 stream分页读取
     *
     * @param list
     * @param page     页码
     * @param pageSize 每页行数
     * @return
     */
    private List<String> readFromPage(List<String> list, int page, int pageSize) {
        ArrayList<String> prevPageList = list.stream()
                .skip((page - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toCollection(ArrayList::new));
        return prevPageList;
    }

    private void setText(List list) {
        if (list != null && !list.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            list.forEach(s -> sb.append(s).append("\n"));
            this.text.setText(sb.toString());
        }
    }

    private void setText(String text) {
        this.text.setText(text);
    }

    public JComponent getContent() {
        return this.book;
    }
}
