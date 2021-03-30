package com.rh.heji.ui.bill.add;

import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.ToastUtils;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Stack;

/**
 * Date: 2020/9/16
 * Author: 锅得铁
 * #
 */
public class Calculation {
    Stack<String> stack = new Stack<>();//输入栈


    MutableLiveData<String> resultLiveData = new MutableLiveData();

    /**
     * 界面输入入栈
     *
     * @param input 0~9 + - .
     */
    public Calculation input(String input) {
        heardClear0(input);
        heardPM();
        if (stack.isEmpty() && inputIsSymbol(input)) {
            return this;
        }

        if (!stack.isEmpty()) {//栈内存在输入
            if (!stack.isEmpty() && lastIsSymbol() && inputIsSymbol(input)) {//末尾为运算符||小数点
                if (stack.lastElement().equals(input)) {//末尾和输入的符号一致
                    return this;
                }
                stack.pop();//不一致，先pop 再push
            }
            if (input.equals(".")) {//输入为小数点
                if (stack.contains(".")) {//栈内包含点
                    int pointCount = (int) stack.stream().filter(s -> s.equals(".")).count();
                    if (stack.firstElement().contains(".")) pointCount += 1;//栈的第一个可能为运算后的小数 小数点+1
                    if (stack.contains("+") || stack.contains("-")) {//+ - 号前如果有点，最多允许后面再有一个点
                        if (pointCount > 1) return this;
                    } else {
                        if (pointCount > 0) return this;
                    }
                }
            }
            if (inputIsOperator(input)) {//输入为运算符+ —
                if (stack.contains("+") || stack.contains("-")) {//栈内已经包含了符号
                    StringBuilder sb = new StringBuilder();
                    stack.forEach(s -> {
                        sb.append(s);
                    });
                    String value = sb.toString();
                    String request = compute(value);
                    stack.clear();
                    stack.push(request);
                }
            }
        }

        if (stack.size() > 14) {
            ToastUtils.showShort("输入这么多干啥？");
            return this;
        }
        stack.push(input);
        result();
        return this;
    }

    /**
     * 计算栈内的值
     *
     * @param value
     * @return 返回最终结果
     */
    @NotNull
    private String compute(String value) {
        String request = new String(value);
        if (value.contains("+")) {
            String v1 = value.substring(0, value.indexOf("+"));
            String v2 = value.substring(value.indexOf("+") + 1, value.length());
            BigDecimal f1 = new BigDecimal(v1);
            BigDecimal f2 = new BigDecimal(v2);
            Math.max(f1.doubleValue(), f2.doubleValue());
            request = f1.add(f2).toString();
        }
        if (value.contains("-")) {
            String v1 = value.substring(0, value.indexOf("-"));
            String v2 = value.substring(value.indexOf("-") + 1, value.length());
            BigDecimal f1 = new BigDecimal(v1);
            BigDecimal f2 = new BigDecimal(v2);
            request = f1.subtract(f2).toString();
        }
        String lastCompute = lastClear0(request);//抹去.00
        return lastCompute;
    }

    /**
     * 头部抹去0
     */
    private void heardClear0(String input) {
        if (input.equals(".")) return;//小数点
        if (!stack.isEmpty() && stack.size() == 1) {
            if (stack.firstElement().equals("0")) {
                stack.remove(0);
                heardClear0(input);
            }
        }
    }

    private String lastClear0(String input) {
        if (input.indexOf(".") > 0) {
            input = input.replaceAll("0+?$", "");//去掉多余的0
            input = input.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return input.trim();
    }

    /**
     * 头部避免+ — 号输入
     */
    private void heardPM() {
        if (!stack.isEmpty() && stack.size() > 0) {
            if (stack.firstElement().equals("-") || stack.firstElement().equals("+")) {
                stack.remove(0);
                heardPM();
            }
        }
    }

    /**
     * 栈顶为符号
     *
     * @return
     */
    private boolean lastIsSymbol() {
        String last = stack.lastElement();
        boolean stackTopSymbol = last.equals("+") || last.equals("-") || last.equals(".");
        return stackTopSymbol;
    }

    /**
     * 栈顶为运算符
     *
     * @return
     */
    private boolean lastIsOperator() {
        String last = stack.lastElement();
        boolean stackTopSymbol = last.equals("+") || last.equals("-");
        return stackTopSymbol;
    }

    /**
     * 栈顶为运算符
     *
     * @return
     */
    private boolean inputIsOperator(String input) {
        boolean inputSymbol = input.equals("+") || input.equals("-");
        return inputSymbol;
    }

    /**
     * 输入的是符号
     *
     * @param input
     * @return
     */
    private boolean inputIsSymbol(String input) {
        boolean inputSymbol = input.equals("+") || input.equals("-") || input.equals(".");
        return inputSymbol;
    }

    public Calculation delete() {
        if (!stack.isEmpty()) {
            stack.pop();
        }
        result();
        return this;
    }

    private void result() {
        if (stack.isEmpty()) {
            resultLiveData.postValue("0");
            return;
        }

        StringBuffer sb = new StringBuffer();
        stack.forEach(s -> sb.append(s));

        resultLiveData.postValue(sb.toString());
    }

    /**
     * 保存最后结果 ==
     */
    public String saveResult() {
        if (lastIsSymbol()) stack.pop();
        String lastResult;
        StringBuilder sb = new StringBuilder();
        stack.forEach(s -> {
            sb.append(s);
        });
        if (stack.contains("+") || stack.contains("-")) {//栈内已经包含了符号
            String value = sb.toString();
            String request = compute(value);
            stack.clear();
            stack.push(request);
            lastResult = request;
            result();
        } else {
            lastResult = sb.toString();
        }
        return lastResult;
    }
}
