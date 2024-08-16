import {build} from "esbuild";
import { promises as fs } from "node:fs";
import * as path from "node:path";

async function bundleModule(
    entry: string,
    module: string,
    outputs: string[]
){
    const result = await build({
        entryPoints: [entry],
        write: false,
        bundle: true,
        outfile:'/index.js',
        format:"cjs",
        external:[
            'kasuga:*'
        ]
    });

    for (let output of outputs) {
        let fullPath = path.resolve(process.cwd(),output);
        let directory = path.dirname(fullPath);
        try {
            await fs.access(directory);
        }catch (e){
            await fs.mkdir(directory, {recursive: true});
        }
        await fs.writeFile(output, result.outputFiles[0].text);
    }
}

function bothServerAndClient(path:string){
    return [
        "src/generated/resources/assets/"+path,
        "src/generated/resources/data/"+path
    ]
}

async function buildBootstrap(){
    await bundleModule(
        "lib/bootstrap/bootstrap.ts",
        "index",
        bothServerAndClient("kasuga_lib/js/bootstrap.js")
    );
}

async function buildModules(){
    const dir = await fs.opendir("lib/modules/")
    for await (const dirent of dir){
        if(dirent.isFile() && dirent.name.endsWith(".ts")){
            let name = dirent.name.slice(0,-3);
            await bundleModule(
                "lib/modules/"+name+".ts",
                "modules/"+name,
                bothServerAndClient("kasuga_lib/js/modules/"+name+".js")
            );
        }
    }

}

async function runBuild(){
    await buildBootstrap();
    await buildModules();
}

runBuild();