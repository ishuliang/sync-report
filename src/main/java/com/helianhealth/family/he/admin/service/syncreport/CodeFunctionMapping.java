package com.helianhealth.family.he.admin.service.syncreport;


import com.helianhealth.family.he.admin.api.wgtj.TijianInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CodeFunctionMapping {
    private static final Map<String, Map<String, Function<TijianInfo, String>>> CODE_FUNCTION = new HashMap<>();

    static {
        // 尿液分析+镜检
        CODE_FUNCTION.put("10003", new HashMap<>() {
            {
                // 蛋白质 -> 尿常规尿蛋白
                put("3798", TijianInfo::getNiaochangGNdb);
                // 葡萄糖(尿) -> 尿常规尿糖
                put("3456", TijianInfo::getNiaochangGNt);
                // 酮体 -> 尿常规尿酮体
                put("1425", TijianInfo::getNiaochangGNtt);
                // 潜血 -> 尿常规尿潜血
                put("4058", TijianInfo::getNiaochangGNqx);
                // 尿微量白蛋白
                put("3355", TijianInfo::getNiaoweiLbdb);
            }
        });

        // 血常规(五分类)(抽血)
        CODE_FUNCTION.put("10300", new HashMap<>() {
            {
                // 血红蛋白HGB -> 血常规血红蛋白
                put("3380", TijianInfo::getXuechangGXhdb);
                // 白细胞WBC -> 血常规白细胞
                put("3859", TijianInfo::getXuechangGBxb);
                // 血小板PLT -> 血常规血小板
                put("1412", TijianInfo::getXuechangGXxb);
            }
        });

        // 肾功四项
        CODE_FUNCTION.put("10382", new HashMap<>() {
            {
                // 肌酐 -> 肾功能血清肌酐
                put("3584", TijianInfo::getShengongNXqjg);
                // 葡萄糖 -> 空腹血糖
                put("3525", TijianInfo::getKongfuXt1);
                // 尿素 -> 肾功能血尿素氮
                put("3589", TijianInfo::getShengongNXnsd);
            }
        });

        // 血脂四项
        CODE_FUNCTION.put("10384", new HashMap<>() {
            {
                // 低密度脂蛋白 -> 血清低密度脂蛋白胆固醇
                put("3142", TijianInfo::getXuezhiXqdmdzdbdgc);
                // 高密度脂蛋白 -> 血清高密度脂蛋白胆固醇
                put("1172", TijianInfo::getXuezhiXqgmdzdbdgc);
                // 甘油三酯
                put("3319", TijianInfo::getXuezhiGysz);
                // 总胆固醇
                put("1171", TijianInfo::getXuezhiZdgc);
            }
        });

        // 肝功能七项
        CODE_FUNCTION.put("10389", new HashMap<>() {
            {
                // 天门冬氨酸氨基转移酶 -> 肝功能血清谷草转氨酶
                put("4042", TijianInfo::getGangongNXqgczam);
                // 丙氨酸氨基转移酶 -> 肝功能血清谷丙转氨酶
                put("4041", TijianInfo::getGangongNXqgbzam);
                // 白蛋白(ALB) -> 肝功能白蛋白
                put("1155", TijianInfo::getGangongNBdb);
            }
        });

        // 一般检查
        CODE_FUNCTION.put("10473", new HashMap<>() {
            {
                // 身高
                put("1000", TijianInfo::getShengao);
                // 体重
                put("1001", TijianInfo::getTizhong);
                // BMI 体重指数
                put("3001", TijianInfo::getBmi);
                // 心率
                put("4114", TijianInfo::getXinlv);
            }
        });

        // 腹部彩超（肝胆脾胰）
        CODE_FUNCTION.put("10571", new HashMap<>() {
            {
                // 腹部彩超（肝胆脾胰） -> B超异常
                put("3412", TijianInfo::getBichaoYc);
            }
        });

        // DR全胸正位片体检专用不含片
        CODE_FUNCTION.put("10651", new HashMap<>() {
            {
                // 胸部正侧位检查 -> 胸部X线片
                put("1478", TijianInfo::getXziongbuXxpyc);
            }
        });
    }

    public static String getFunction(String itemId, String nodeId,TijianInfo info) {
        if (!CODE_FUNCTION.containsKey(itemId)) {
            return null;
        }
        if (!CODE_FUNCTION.get(itemId).containsKey(nodeId)) {
            return null;
        }
        return CODE_FUNCTION.get(itemId).get(nodeId).apply(info);
    }

}
