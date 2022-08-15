package com.m2r.codegen.parser.template.actions;

import com.m2r.codegen.parser.template.Param;

import java.util.Scanner;

public class CaseAction implements MethodAction {

    public static final String REPLACE_MARK = "_INSERT_SUB_CONTENT_HERE_";

    @Override
    public void validate(ActionState state) throws RuntimeException {
        if (state.getMethod().getParameters().size() != 4)
            throw new RuntimeException("UseIf method required 4 parameters: \n" +
                    "- startLine\n- endLine\n- method\n- value") ;
    }

    @Override
    public void process(ActionState state) throws Exception {
        Integer startLine = Integer.parseInt(state.getMethod().getParameter(0).getValue());
        Integer endLine = Integer.parseInt(state.getMethod().getParameter(1).getValue());
        Param methodParam = state.getMethod().getParameter(2);
        Param valueParam = state.getMethod().getParameter(3);

        int start = startLine - state.getBlock().getLineStart();
        int end = endLine -state.getBlock().getLineStart();
        StringBuilder blockContent = readLines(start, end, state.getContent().toString());
        state.getMethod().getContext().put("param1", valueParam.getValue());
        boolean show = methodParam.resolveValueToBoolean(state.getMethod().getContext(), false);
        if (show) {
            ActionState subAction = new ActionState(state.getBlock(), state.getMethod(),state.getLevel() + 1, blockContent);
            processChildrenMethods(subAction, state.getLevel() + 1, 0, 0);
            state.getContent().setLength(0);
            state.getContent().append(subAction.getContent());
        }
        else {
            state.getContent().setLength(0);
        }
    }

    private StringBuilder readLines(int start, int end, String content) {
        StringBuilder result = new StringBuilder();
        Scanner scanner = new Scanner(content);
        int num = 0;
        String line = null;
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (num >= start && num <= end) {
                result.append(line + "\n");
            }
            else if (num > end) {
                break;
            }
            num++;
        }
        return result;
    }

}
