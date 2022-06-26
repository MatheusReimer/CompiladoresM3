package compiladoresM2;

import java.util.ArrayList;
import java.util.List;

public class FunctionsAndProcedures {
    public ArrayList<Function> listOfFunctions = new ArrayList<>();


    private Function getFunctionByName(String name){
        for(Function x : listOfFunctions){
            if(x.name.equals(name)){
                return x;
            }
        }
        return  null;
    }
    public void startProcess(String name){
        BranchCounter obj = new BranchCounter();
        Main.branch.branchCounter++;
        obj.lastBranchType = "func";
        obj.number= Main.branch.branchCounter;
        Main.branch.branchPile.add(obj);
        Main.sem.assembly.funcLabel(Main.sem.assembly, (Main.branch.branchPile.get(Main.branch.branchPile.size()-1).number) + name);
    }
    public void continueProcessParameter(){

    }
    public void endFuncProcess(){
        Main.branch.branchPile.remove(Main.branch.branchPile.size()-1);
    }
    public void callFunc(){

        String lastFuncion = Main.sem.calledFunction;
        Function func = getFunctionByName(lastFuncion);
        for(int i=0;i<Main.sem.calledParameters.size();i++){
            Main.sem.assembly.simpleVarReceivesIntOrVar(Main.sem.calledParameters.get(i),Main.sem.assembly);
            Main.sem.assembly.store(func.parameterList.get(i*2+1),Main.sem.assembly,false,func);/// CHANGE TO TRUE IF IS VEC
        }
        Main.sem.assembly.call(lastFuncion,Main.sem.assembly);
        if(Main.sem.isAttr){
            Main.sem.assembly.store(Main.sem.elementOnTheLeftSideOfAttr.name,Main.sem.assembly,false);
        }

    }

}
