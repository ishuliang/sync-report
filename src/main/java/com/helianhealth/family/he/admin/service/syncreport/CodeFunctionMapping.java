package com.helianhealth.family.he.admin.service.syncreport;


import com.helianhealth.family.he.admin.api.wgtj.TijianInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CodeFunctionMapping {
    private static final Map<String, Map<String, Function<TijianInfo, String>>> CODE_FUNCTION = new HashMap<>();

    static {
        // 一般检查
        CODE_FUNCTION.put("83883189", new HashMap<>() {
            {
                // 体检医生ID或中文名字
                put("zerenYsFid", TijianInfo::getZerenYsFid);
                // 症状（默认"01"无）
                put("zhengzhuang", TijianInfo::getZhengzhuang);
                // 症状其他
                put("zhengzhuangQt", TijianInfo::getZhengzhuangQt);
                // 体温
                put("tiwen", TijianInfo::getTiwen);
                // 脉率
                put("mailv", TijianInfo::getMailv);
                // 呼吸频率
                put("huxiPl", TijianInfo::getHuxiPl);
                // 左侧高血压
                put("xueyaZcg", TijianInfo::getXueyaZcg);
                // 左侧低血压
                put("xueyaZcd", TijianInfo::getXueyaZcd);
                // 右侧高血压
                put("xueyaYcg", TijianInfo::getXueyaYcg);
                // 右侧低血压
                put("xueyaYcd", TijianInfo::getXueyaYcd);
                // 身高
                put("shengao", TijianInfo::getShengao);
                // 体重
                put("tizhong", TijianInfo::getTizhong);
                // 腰围
                put("yaowei", TijianInfo::getYaowei);
                // 体重指数
                put("bmi", TijianInfo::getBmi);
                // 老年人健康状态自我评估
                put("laonianRjkzt", TijianInfo::getLaonianRjkzt);
                // 老年人生活自理能力自我评估
                put("laonianRshzlnl", TijianInfo::getLaonianRshzlnl);
                // 老年人认知功能
                put("laonianRrzgn", TijianInfo::getLaonianRrzgn);
                // 简易智力状态检查总分
                put("laonianRjyzljcZf", TijianInfo::getLaonianRjyzljcZf);
                // 老年人情感状态
                put("laonianRqgzt", TijianInfo::getLaonianRqgzt);
                // 老年人抑郁评分检查总分
                put("laonianRyyjcZf", TijianInfo::getLaonianRyyjcZf);
                // 体育锻炼频率
                put("tiyuDlpl", TijianInfo::getTiyuDlpl);
                // 体育锻炼时间
                put("tiyuDlsj", TijianInfo::getTiyuDlsj);
                // 坚持锻炼时间
                put("jianchiDlsj", TijianInfo::getJianchiDlsj);
                // 锻炼方式
                put("duanlianFs", TijianInfo::getDuanlianFs);
                // 饮食习惯
                put("yinshiXg", TijianInfo::getYinshiXg);
                // 吸烟状况
                put("xiyanZk", TijianInfo::getXiyanZk);
                // 日吸烟量
                put("rixiYl", TijianInfo::getRixiYl);
                // 开始吸烟年龄
                put("kaishiXynl", TijianInfo::getKaishiXynl);
                // 戒烟年龄
                put("xiyanNl", TijianInfo::getXiyanNl);
                // 饮酒频率
                put("yinjiuPl", TijianInfo::getYinjiuPl);
                // 日饮酒量
                put("riyinJl", TijianInfo::getRiyinJl);
                // 是否戒酒
                put("shifouJj", TijianInfo::getShifouJj);
                // 戒酒年龄
                put("jiejiuNl", TijianInfo::getJiejiuNl);
                // 开始饮酒年龄
                put("kaishiYjnl", TijianInfo::getKaishiYjnl);
                // 近一年内是否曾醉酒
                put("jinyiNnsfczj", TijianInfo::getJinyiNnsfczj);
                // 饮酒种类
                put("yinjiuZl", TijianInfo::getYinjiuZl);
                // 饮酒种类其他
                put("yinjiuZlQt", TijianInfo::getYinjiuZlQt);
                // 职业病危害因素接触史
                put("zhiyeBwhysjcs", TijianInfo::getZhiyeBwhysjcs);
                // 工种
                put("gongzhong", TijianInfo::getGongzhong);
                // 从业时间
                put("congyeSj", TijianInfo::getCongyeSj);
                // 毒物种类粉尘
                put("duwuZlFc", TijianInfo::getDuwuZlFc);
                // 粉尘防护措施
                put("fanghuCsFc", TijianInfo::getFanghuCsFc);
                // 有粉尘防护措施
                put("fanghuCsFcy", TijianInfo::getFanghuCsFcy);
                // 毒物种类放射物质
                put("duwuZlFswz", TijianInfo::getDuwuZlFswz);
                // 放射物质防护措施
                put("fanghuCsFswz", TijianInfo::getFanghuCsFswz);
                // 有放射物质防护措施
                put("fanghuCsFswzy", TijianInfo::getFanghuCsFswzy);
                // 毒物种类物理因素
                put("duwuZlWlys", TijianInfo::getDuwuZlWlys);
                // 物理因素防护措施
                put("fanghuCsWlys", TijianInfo::getFanghuCsWlys);
                // 有物理因素防护措施
                put("fanghuCsWlysy", TijianInfo::getFanghuCsWlysy);
                // 毒物种类化学物质
                put("duwuZlHxwz", TijianInfo::getDuwuZlHxwz);
                // 化学物质防护措施
                put("fanghuCsHxwz", TijianInfo::getFanghuCsHxwz);
                // 有化学物质防护措施
                put("fanghuCsHxwzy", TijianInfo::getFanghuCsHxwzy);
                // 毒物种类其他
                put("duwuZlQt", TijianInfo::getDuwuZlQt);
                // 其他防护措施
                put("fanghuCsQt", TijianInfo::getFanghuCsQt);
                // 有其他防护措施
                put("fanghuCsQty", TijianInfo::getFanghuCsQty);
                // 脑血管疾病NXGJB，默认"01"无）
                put("naoxueGjb", TijianInfo::getNaoxueGjb);
                // 脑血管疾病其他
                put("naoxueGjbqt", TijianInfo::getNaoxueGjbqt);
                // 肾脏疾病SZJB，默认"01"无）
                put("shenzangJb", TijianInfo::getShenzangJb);
                // 肾脏疾病其他
                put("shenzangJbqt", TijianInfo::getShenzangJbqt);
                // 心脏疾病XZJB，默认"01"无）
                put("xinzangJb", TijianInfo::getXinzangJb);
                // 心脏疾病其他
                put("xinzangJbqt", TijianInfo::getXinzangJbqt);
                // 血管疾病
                put("xueguanJb", TijianInfo::getXueguanJb);
                // 血管疾病其他
                put("xueguanJbqt", TijianInfo::getXueguanJbqt);
                // 眼部疾病
                put("yanbuJb", TijianInfo::getYanbuJb);
                // 眼部疾病其他
                put("yanbuJbqt", TijianInfo::getYanbuJbqt);
                // 神经系统疾病
                put("shenjingJb", TijianInfo::getShenjingXtjb);
                // 神经系统疾病其他
//                put("shenjingJbqt", TijianInfo::getshenjingxt);
                // 其他系统疾病其他
                put("xitongJbqt", TijianInfo::getQitaXtjb);
                // 就医医师
//                put("jiuyiYs", TijianInfo::getjiu);
                // 就医科室编码
//                put("jiuyiKsbm", TijianInfo::getjiu);
//                // 就医科室名称
//                put("jiuyiKsmc", TijianInfo::getJiuyiKsmc);
                // 健康指导意见
                put("zhidaoYijian", TijianInfo::getJiankangZdCh);
            }
        });

        // 内科
        CODE_FUNCTION.put("83900712", new HashMap<>() {
            {
                // 口唇
                put("kouchun", TijianInfo::getKouchun);
                // 齿列
                put("chilie", TijianInfo::getChilie);
                // 缺齿左上
                put("quechiZs", TijianInfo::getQuechiZs);
                // 缺齿左下
                put("quechiZx", TijianInfo::getQuechiZx);
                // 缺齿右上
                put("quechiYs", TijianInfo::getQuechiYs);
                // 缺齿右下
                put("quechiYx", TijianInfo::getQuechiYx);
                // 龋齿左上
                put("quchiZs", TijianInfo::getQuchiZs);
                // 龋齿左下
                put("quchiZx", TijianInfo::getQuchiZx);
                // 龋齿右上
                put("quchiYs", TijianInfo::getQuchiYs);
                // 龋齿右下
                put("quchiYx", TijianInfo::getQuchiYx);
                // 义齿即假牙-左上
                put("yichiZs", TijianInfo::getYichiZs);
                // 义齿即假牙-左下
                put("yichiZx", TijianInfo::getYichiZx);
                // 义齿即假牙-右上
                put("yichiYs", TijianInfo::getYichiYs);
                // 义齿即假牙-右下
                put("yichiYx", TijianInfo::getYichiYx);
                // 咽部
                put("yanbu", TijianInfo::getYanbu);
                // 听力
                put("tingli", TijianInfo::getTingli);
                // 运动功能
                put("yundongGn", TijianInfo::getYundongGn);
                // 皮肤（默认"01"正常）
                put("pifu", TijianInfo::getPifu);
                // 皮肤异常
                put("pifuYc", TijianInfo::getPifuYc);
                // 巩膜
                put("gongmo", TijianInfo::getGongmo);
                // 巩膜其他
                put("gongmoQt", TijianInfo::getGongmoQt);
                // 淋巴结
                put("linbaJ", TijianInfo::getLinbaJ);
                // 淋巴结其他
                put("linbaJqt", TijianInfo::getLinbaJqt);
                // 桶状胸（默认"0"无）
                put("tongzhuangX", TijianInfo::getTongzhuangX);
                // 呼吸音
                put("huxiY", TijianInfo::getHuxiY);
                // 呼吸音异常
                put("huxiYyc", TijianInfo::getHuxiYyc);
                // 罗音
                put("luoyin", TijianInfo::getLuoyin);
                // 罗音其他
                put("luoyinQt", TijianInfo::getLuoyinQt);
                // 心率
                put("xinlv", TijianInfo::getXinlv);
                // 心律整齐
                put("xinlvZq", TijianInfo::getXinlvZq);
                // 杂音
                put("zayin", TijianInfo::getZayin);
                // 有杂音
                put("zayinY", TijianInfo::getZayinY);
                // 腹部压痛
                put("fubuYt", TijianInfo::getFubuYt);
                // 腹部压痛有
                put("fubuYty", TijianInfo::getFubuYty);
                // 腹部包块
                put("fubuBk", TijianInfo::getFubuBk);
                // 腹部包块有
                put("fubuBky", TijianInfo::getFubuBky);
                // 腹部肝大
                put("fubuGd", TijianInfo::getFubuGd);
                // 腹部肝大有
                put("fubuGdy", TijianInfo::getFubuGdy);
                // 腹部脾大
                put("fubuPd", TijianInfo::getFubuPd);
                // 腹部脾大有
                put("fubuPdy", TijianInfo::getFubuPdy);
                // 腹部移动性浊音
                put("fubuYdxzy", TijianInfo::getFubuYdxzy);
                // 腹部移动性浊音有
                put("fubuYdxzyy", TijianInfo::getFubuYdxzyy);
                // 下肢水肿
                put("xiazhiSz", TijianInfo::getXiazhiSz);
                // 足背动脉搏动
                put("zubeiDmbd", TijianInfo::getZubeiDmbd);
                // 查体其他
                put("chatiQt", TijianInfo::getChatiQt);
            }
        });

        // 外科
        CODE_FUNCTION.put("83883186", new HashMap<>() {
            {
                // 肛门指诊
                put("gangmenZz", TijianInfo::getGangmenZz);
                // 肛门指诊其他
                put("gangmenZzqt", TijianInfo::getGangmenZzqt);
                // 乳腺
                put("ruxian", TijianInfo::getRuxian);
                // 乳腺其他
                put("ruxianQt", TijianInfo::getRuxianQt);
            }
        });

        // 眼科
        CODE_FUNCTION.put("99731473", new HashMap<>() {
            {
                // 左眼视力
                put("shiliZy", TijianInfo::getShiliZy);
                // 右眼视力
                put("shiliYy", TijianInfo::getShiliYy);
                // 矫正视力左眼
                put("jiaozhengSlZy", TijianInfo::getJiaozhengSlZy);
                // 矫正视力右眼
                put("jiaozhengSlYy", TijianInfo::getJiaozhengSlYy);
                // 眼底
                put("yandi", TijianInfo::getYandi);
                // 眼底异常
                put("yandiYc", TijianInfo::getYandiYc);
            }
        });

        // 体电图室
        CODE_FUNCTION.put("99731453", new HashMap<>() {
            {
                // 心电图
                put("xindianT", TijianInfo::getXindianT);
                // 心电图异常
                put("xindianTyc", TijianInfo::getXindianTyc);
            }
        });

        // 检验科
        CODE_FUNCTION.put("83883187", new HashMap<>() {
            {
                // 血常规血红蛋白
                put("xuechangGXhdb", TijianInfo::getXuechangGXhdb);
                // 血常规白细胞
                put("xuechangGBxb", TijianInfo::getXuechangGBxb);
                // 血常规血小板
                put("xuechangGXxb", TijianInfo::getXuechangGXxb);
                // 血常规其他
                put("xuechangGQt", TijianInfo::getXuechangGQt);
                // 尿常规尿蛋白
                put("niaochangGNdb", TijianInfo::getNiaochangGNdb);
                // 尿常规尿糖
                put("niaochangGNt", TijianInfo::getNiaochangGNt);
                // 尿常规尿酮体
                put("niaochangGNtt", TijianInfo::getNiaochangGNtt);
                // 尿常规尿潜血
                put("niaochangGNqx", TijianInfo::getNiaochangGNqx);
                // 尿常规其他
                put("niaochangGQt", TijianInfo::getNiaochangGQt);
                // 尿微量白蛋白
                put("niaoweiLbdb", TijianInfo::getNiaoweiLbdb);
                // 大便潜血
                put("dabianQx", TijianInfo::getDabianQx);
                // 乙型肝炎表面抗原
                put("yxgybmky", TijianInfo::getYxgybmky);
                // 肝功能血清谷丙转氨酶
                put("gangongNXqgbzam", TijianInfo::getGangongNXqgbzam);
                // 肝功能血清谷草转氨酶
                put("gangongNXqgczam", TijianInfo::getGangongNXqgczam);
                // 肝功能白蛋白
                put("gangongNBdb", TijianInfo::getGangongNBdb);
                // 肝功能总胆红素
                put("gangongNZdhs", TijianInfo::getGangongNZdhs);
                // 肝功能结合胆红素
                put("gangongNJhdhs", TijianInfo::getGangongNJhdhs);
                // 肾功能血清肌酐
                put("shengongNXqjg", TijianInfo::getShengongNXqjg);
                // 肾功能血尿素氮
                put("shengongNXnsd", TijianInfo::getShengongNXnsd);
                // 肾功能血钾浓度
                put("shengongNXjnd", TijianInfo::getShengongNXjnd);
                // 肾功能血钠浓度
                put("shengongNXnnd", TijianInfo::getShengongNXnnd);
                // 总胆固醇
                put("xuezhiZdgc", TijianInfo::getXuezhiZdgc);
                // 甘油三酯
                put("xuezhiGysz", TijianInfo::getXuezhiGysz);
                // 血清低密度脂蛋白胆固醇
                put("xuezhiXqdmdzdbdgc", TijianInfo::getXuezhiXqdmdzdbdgc);
                // 血清高密度脂蛋白胆固醇
                put("xuezhiXqgmdzdbdgc", TijianInfo::getXuezhiXqgmdzdbdgc);
            }
        });

        // 糖尿病筛查
        CODE_FUNCTION.put("99731475", new HashMap<>() {
            {
                // 空腹血糖1
                put("kongfuXt1", TijianInfo::getKongfuXt1);
                // 空腹血糖2
                put("kongfuXt2", TijianInfo::getKongfuXt2);
                // 糖化血红蛋白
                put("tanghuaXhdb", TijianInfo::getTanghuaXhdb);
            }
        });

        // 放射科
        CODE_FUNCTION.put("99731464", new HashMap<>() {
            {
                // 胸部X线片
                put("xiongbuXxp", TijianInfo::getXiongbuXxp);
                // 胸部X线片异常
                put("xziongbuXxpyc", TijianInfo::getXziongbuXxpyc);
            }
        });

        // 多功能检查室
        CODE_FUNCTION.put("99731462", new HashMap<>() {
            {
                // B超
                put("bichao", TijianInfo::getBichao);
                // B超异常
                put("bichaoYc", TijianInfo::getBichaoYc);
                // B超其他
                put("bichaoQt", TijianInfo::getBichaoQt);
                // B超其他异常
                put("bichaoQtYc", TijianInfo::getBichaoQtYc);
            }
        });

        // 其他
        CODE_FUNCTION.put("00000000", new HashMap<>() {
            {
                // 妇科外阴
                put("fukeWy", TijianInfo::getFukeWy);
                // 妇科外阴异常
                put("fukeWyyc", TijianInfo::getFukeWyyc);
                // 妇科阴道
                put("fukeYd", TijianInfo::getFukeYd);
                // 妇科阴道异常
                put("fukeYdyc", TijianInfo::getFukeYdyc);
                // 妇科宫颈
                put("fukeGj", TijianInfo::getFukeGj);
                // 妇科宫颈异常
                put("fukeGjyc", TijianInfo::getFukeGjyc);
                // 妇科宫体
                put("fukeGt", TijianInfo::getFukeGt);
                // 妇科宫体异常
                put("fukeGtyc", TijianInfo::getFukeGtyc);
                // 妇科附件
                put("fukeFj", TijianInfo::getFukeFj);
                // 妇科附件异常
                put("fukeFjyc", TijianInfo::getFukeFjyc);
                // 宫颈涂片
                put("gongjingTp", TijianInfo::getGongjingTp);
                // 宫颈涂片异常
                put("gongjingTpyc", TijianInfo::getGongjingTpyc);
                // 辅助检查其他
                put("fuzhuJcqt", TijianInfo::getFuzhuJcqt);
                // 其他说明
//                put("qitaSm", TijianInfo::getQitaXtjb);
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
