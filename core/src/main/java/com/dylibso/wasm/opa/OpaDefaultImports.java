package com.dylibso.wasm.opa;

import com.dylibso.chicory.runtime.Memory;
import com.dylibso.chicory.wasm.types.MemoryLimits;
import java.util.Map;

// to be implemented by the user
public class OpaDefaultImports implements OpaImports {
    // TODO: review the default min, max limits
    protected final Memory memory;
    protected OpaBuiltin.Builtin[] builtins = new OpaBuiltin.Builtin[0];

    public OpaDefaultImports() {
        this(10, MemoryLimits.MAX_PAGES);
    }

    public OpaDefaultImports(OpaBuiltin.Builtin... builtins) {
        this(10, MemoryLimits.MAX_PAGES, builtins);
    }

    public OpaDefaultImports(int initial, int maximum) {
        this.memory = new Memory(new MemoryLimits(initial, maximum));
    }

    public OpaDefaultImports(int initial, int maximum, OpaBuiltin.Builtin... builtins) {
        this.builtins = builtins;
        this.memory = new Memory(new MemoryLimits(initial, maximum));
    }

    @Override
    public void initializeBuiltins(Map<String, Integer> mappings) {
        var result = new OpaBuiltin.Builtin[mappings.size()];
        // Default initialization to have proper error messages
        for (var m : mappings.entrySet()) {
            result[m.getValue()] = () -> m.getKey();
        }
        for (var builtin : this.builtins) {
            if (mappings.containsKey(builtin.name())) {
                result[mappings.get(builtin.name())] = builtin;
            }
        }
        this.builtins = result;
    }

    public Memory memory() {
        return memory;
    }

    @Override
    public void opaPrintln(OpaWasm wasm, int ptr) {
        var message = wasm.memory().readCString(ptr);
        System.out.println("opa_println - " + message);
    }

    @Override
    public void opaAbort(OpaWasm instance, int ptr) {
        var errorMessage = instance.memory().readCString(ptr);
        throw new RuntimeException("opa_abort - " + errorMessage);
    }

    @Override
    public int opaBuiltin0(OpaWasm instance, int builtinId, int ctx) {
        return builtins[builtinId].asBuiltin0(instance);
    }

    @Override
    public int opaBuiltin1(OpaWasm instance, int builtinId, int ctx, int _1) {
        return builtins[builtinId].asBuiltin1(instance, _1);
    }

    @Override
    public int opaBuiltin2(OpaWasm instance, int builtinId, int ctx, int _1, int _2) {
        return builtins[builtinId].asBuiltin2(instance, _1, _2);
    }

    @Override
    public int opaBuiltin3(OpaWasm instance, int builtinId, int ctx, int _1, int _2, int _3) {
        return builtins[builtinId].asBuiltin3(instance, _1, _2, _3);
    }

    @Override
    public int opaBuiltin4(
            OpaWasm instance, int builtinId, int ctx, int _1, int _2, int _3, int _4) {
        return builtins[builtinId].asBuiltin4(instance, _1, _2, _3, _4);
    }
}
