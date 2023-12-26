import org.json.simple.parser.ParseException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MainForm {
    private JFrame frame;
    private JPanel panel;
    private JComboBox parametr1;
    private JComboBox query_type;
    private JComboBox parametr2;
    private JButton ok;
    private JLabel answer;

    //содаем массив типов запросов
    String[] communs = {"Является ли видом (ako)", "Является ли частью (is-a)", "Имеет ли (has-a)"};
    String[] edges = {
            "разговоры", "частые разговоры", "нечастые разговоры", "долгие разговоры", "недолгие разговоры",
            "использование смс", "использование смс: да", "использование смс: нет",
            "общение через смс", "общение через смс: редко", "общение через смс: иногда", "общение через смс: часто",
            "пакет минут", "100 минут", "250 минут", "500 минут", "пакет смс", "0 смс", "100 смс", "250 смс", "500 смс",
            "тарифы", "Стандарт", "Выгодный", "На связи", "Месседж", "Поминутный", "Премиум",
            "Выгодный + 100 минут", "Выгодный + 200 минут", "Выгодный + 100 смс", "Выгодный + 200 смс",
            "Поминутный + 100 минут", "Поминутный + 200 минут"
    };

    public MainForm()  {
        frame = new JFrame("Система выбора тарифа");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800, 280);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        parametr1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        query_type.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        parametr2.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        //заполняем выпадающие списки
        for (int i = 0; i < edges.length; i++) {
            parametr1.addItem(edges[i]);
            parametr2.addItem(edges[i]);
        }
        for (int i = 0; i < communs.length; i++) {
            query_type.addItem(communs[i]);
        }

        ok.setFocusPainted(false);
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                QueryResult qr = new QueryResult();
                try {
                    //выводим на форму результат
                    answer.setText("Ответ: "+qr.getResult(parametr1.getSelectedIndex(), parametr2.getSelectedIndex(), query_type.getSelectedIndex()));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
}