import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class QueryResult {
    ArrayList<String> list = new ArrayList<>();

    //создаем массив узлов сети
    String[] edges = {
            "разговоры", "частые", "нечастые", "долгие", "недолгие",
            "использование смс", "да", "нет", "общение через смс", "редко", "иногда", "часто",
            "пакет минут", "100 mins", "250 mins", "500 mins", "пакет смс", "0 sms", "100 sms", "250 sms", "500 sms",
            "тарифы", "Стандарт", "Выгодный", "На связи", "Месседж", "Поминутный", "Премиум",
            "Выгодный + 100 минут", "Выгодный + 200 минут", "Выгодный + 100 смс", "Выгодный + 200 смс",
            "Поминутный + 100 минут", "Поминутный + 200 минут"
    };

    String getResult(int param1, int param2, int q_type) throws IOException, ParseException {
        //из файла nodes запись в массив nodes
        FileReader fr = new FileReader("src/nodes.json");
        Object all_nodes = new JSONParser().parse(fr);
        fr.close();
        JSONObject j_all_nodes = (JSONObject) all_nodes;
        JSONArray nodes = (JSONArray) j_all_nodes.get("nodes");

        //в список добавляем 1 выбранный узел
        list.add(edges[param1]);


        //переменная, указывающая, найден ли 2 выбранный узел
        int find;

        //переменная, указывающая, продолжать ли цикл (поиск)
        int contin = 0;

        //начинаем цикл
        do {
            //поиск по узлам сети
            int[] result_mas = search_nodes(nodes, edges[param2], q_type);
            find = result_mas[0];
            contin = result_mas[1];
        } while (contin == 1);
        //если тип запроса has-a и не найден 2 выбранный узел
        if (q_type == 2 && find != 1) {
            list.clear();
            list.add(edges[param1]);
            ArrayList<String> list2 = new ArrayList<>();
            //ищем его у детей 1 выбранного узла
            for(int i = 0; i < list.size(); i++) {
                for(int j = 0; j < nodes.size(); j++) {
                    String res = suit_node((JSONObject)nodes.get(j), list.get(i), 0);
                    if (res != null) {
                        list2.add(res);
                    }
                }
            }
            list = list2;
            contin = 0;
            do {
                int[] result_mas = search_nodes(nodes, edges[param2], q_type);
                find = result_mas[0];
                contin = result_mas[1];
            } while (contin == 1);
        }
        if (find == 1) {
            return "да";
        } else {
            return "нет";
        }
    }

    private int[] search_nodes(JSONArray nodes, String select_node, int q_type) {
        int find = -1, contin;
        ArrayList<String> list2 = new ArrayList<>();
        for(int i = 0; i < list.size(); i++) {
            for(int j = 0; j < nodes.size(); j++) {

                //подходит ли конкретный узел
                String res = suit_node((JSONObject)nodes.get(j), list.get(i), q_type);
                if (res != null) {
                    list2.add(res);
                }
            }
        }
        if (list2.size() == 0) {
            find = 0;
            contin = 0;
        } else if (list2.contains(select_node)) {
            find = 1;
            contin = 0;
        } else {
            contin = 1;
            list = list2;
        }
        return new int[] {find, contin};
    }
    private String suit_node(JSONObject j_ob, String select_node, int q_type) {
        int count = 0;
        String res = "";
        Iterator<String> keys = j_ob.keySet().iterator();

        //проходимся по ключам объекта json
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (Objects.equals(key, "comm")) {

                //проверяем тип запроса
                switch (q_type) {
                    case 0:
                    case 1:
                        //ako или is-a
                        if (Objects.equals(j_ob.get(key).toString(), "ako") || Objects.equals(j_ob.get(key).toString(), "is-a")) {
                            count++;
                        }
                        break;
                    case 2:
                        //has-a
                        if (Objects.equals(j_ob.get(key).toString(), "has-a")) {
                            count++;
                        }
                        break;
                }
            }

            //если 1 выбранный узел является родителем
            if (count == 1 && Objects.equals(key, "from") && Objects.equals(j_ob.get(key).toString(), select_node)) {
                count++;
            }
            if (count == 2 && Objects.equals(key, "to")) {
                //получаем значение ключа 'to'
                res = (String) j_ob.get(key);
            } else {
                res = null;
            }
        }
        return res;
    }
}