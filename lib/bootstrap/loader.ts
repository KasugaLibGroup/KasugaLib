import {} from "./interface"

const require = function(moduleName:string):any{
    return globalThis['__KASUGA_REQUIRE__'].require(moduleName);
}

Object.defineProperty(globalThis,"require",{
    value:require
});

export {require};