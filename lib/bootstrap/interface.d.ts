declare global{
    var __KASUGA_REQUIRE__ : {require:(module:string)=>any};
    var require: (module:string)=>any;

    var _require_: (module:string)=>any;
}

export {}