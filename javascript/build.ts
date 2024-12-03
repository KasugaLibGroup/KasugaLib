import {Glob} from 'glob';
import {promises as fs} from "fs";
import { resolve, join, resolve, dirname } from "path"
import spawn from 'execa'
export function spawnAsync(args: string[], options: spawn.Options = {}) {
    const child = spawn(args[0], args.slice(1), options)
    return new Promise<number>((resolve) => {
        child.stderr?.pipe(process.stderr)
        child.stdout?.pipe(process.stdout)
        child.on('close', resolve)
    })
}
async function compileCore(){
    console.info(" * Compiling Core ....")
    await spawnAsync(["yarn"], { cwd: resolve(__dirname, "core") })
    await spawnAsync(["yarn", "yakumo", "tsc"], { cwd: resolve(__dirname, "core") })
    console.info(" * Compiled Core")
}

async function copyCore(){
    console.info(" * Copying Core ....")
    const root = resolve(__dirname, 'core');
    const coreGlobs = ['**/*.js', '**/package.json','!**/node_modules/**'];
    const matched:Set<string> = new Set;
    const ignored:Set<string> = new Set;
    const mapper:Map<string, Path> = new Map;
    for(let glob of coreGlobs){
        const isIgnore = glob.startsWith('!');
        if(isIgnore){
            glob = glob.slice(1);
        }
        const files = new Glob(glob, { withFileTypes: true, cwd:root });
        for await (const file of files){
            const stat = await fs.stat(file.fullpath());
            if(stat.isDirectory()) {
                continue;
            }

            if(isIgnore){
                ignored.add(file.relative());
                console.info("Ignore: " + file.relative());
            }else{
                matched.add(file.relative());
                console.info("Matched: " + file.relative());
                mapper.set(file.relative(), file);
            }
        }
    }
    ignored.forEach((s)=>matched.delete(s));
    console.info(" + Added: " + matched.size);

    const destDir = resolve(__dirname, "../src/generated/resources/script");
    await fs.rm(destDir, { recursive: true, force: true });
    const tasks = Array.from(matched).map(async (file)=>{
        const src = mapper.get(file);
        const dest = join(destDir, file);
        await fs.mkdir(dirname(dest), { recursive: true });
        await fs.copyFile(src.fullpath(), dest);
    });
    fs.writeFile(join(destDir, ".gitignore"), "*");
    await Promise.all(tasks);
    console.info(" * Copied Core")
}

async function buildCore(){
    await compileCore();
    await copyCore();
}

buildCore();