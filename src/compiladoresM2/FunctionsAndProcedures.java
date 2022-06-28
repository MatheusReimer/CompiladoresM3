package compiladoresM2;

import java.util.ArrayList;
import java.util.List;

public class FunctionsAndProcedures {
    public ArrayList<Function> listOfFunctions = new ArrayList<>();


    public Function getFunctionByName(String name){
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
    public void handleReturn(ArrayList<String> list){
        if(Main.sem.checkIfElIsAParameterFromCurrentFunc(list.get(0))){
            Main.sem.assembly.simpleVarReceivesIntOrVar(list.get(0)+"_"+listOfFunctions.get(listOfFunctions.size()-1).name,Main.sem.assembly);
        }else{
            Main.sem.assembly.simpleVarReceivesIntOrVar(list.get(0),Main.sem.assembly);
        }
        for(int i =1;i<list.size();i=i+2){
            if(Main.sem.checkIfElIsAParameterFromCurrentFunc(list.get(i+1))){
                Main.sem.assembly.addToIndex(list.get(i+1)+"_"+listOfFunctions.get(listOfFunctions.size()-1).name,list.get(i),Main.sem.assembly);
            }else{
                Main.sem.assembly.addToIndex(list.get(i+1),list.get(i),Main.sem.assembly);
            }
        }
        Main.sem.assembly.returnF(Main.sem.assembly);
    }

    public void handleVoid(){
        Main.sem.assembly.returnF(Main.sem.assembly);
    }
}
