package com.m2r.codegen.parser.template.actions;

public class DelimiterAction implements MethodAction {

    @Override
    public void validate(ActionState state) throws RuntimeException {
        if (state.getMethod().getParameters().size() < 3)
            throw new RuntimeException("Iterate method required 3 parameters: \n" +
                    "- prefix\n- divider\n- suffix") ;
    }

    @Override
    public void process(ActionState state) throws Exception {
        String prefix = state.getMethod().getParameter(0).getValue();
        String divider = state.getMethod().getParameter(1).getValue();
        String suffix = state.getMethod().getParameter(2).getValue();
        if (state.getIndex() == 0) {
            int i = 0;
            String str = state.getContent().toString();
            while (i < str.length()) {
                char c = str.charAt(i++);
                if (c != '\n' && c != '\r' && c != '\t') {
                    i--;
                    break;
                }
            }
            state.getContent().insert(i, prefix);
        }
        if (state.getIndex() == (state.getSize() - 1)) {
            state.getContent().insert(state.getContent().length() - 1, suffix);
        }
        else {
            state.getContent().insert(state.getContent().length() - 1, divider);
        }
    }

}
