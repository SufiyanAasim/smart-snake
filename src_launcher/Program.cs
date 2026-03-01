using System;
using System.Diagnostics;
using System.IO;

namespace SmartSnakeGameLauncher
{
    class Program
    {
        static void Main(string[] args)
        {
            try
            {
                string baseDir = AppDomain.CurrentDomain.BaseDirectory;
                string jarPath = Path.Combine(baseDir, "dist", "SmartSnakeGame.jar");
                string libDir = Path.Combine(baseDir, "lib");
                
                string classpath = $"{jarPath};{Path.Combine(libDir, "sqlite-jdbc.jar")};{Path.Combine(libDir, "slf4j-api.jar")};{Path.Combine(libDir, "slf4j-simple.jar")}";

                ProcessStartInfo startInfo = new ProcessStartInfo();
                
                // Locate java or javaw (javaw hides the console window natively)
                string defaultJava = @"C:\Program Files\Android\openjdk\jdk-21.0.8\bin\javaw.exe";
                if (File.Exists(defaultJava))
                {
                    startInfo.FileName = defaultJava;
                }
                else
                {
                    startInfo.FileName = "javaw.exe"; // Fallback to system PATH
                }

                startInfo.Arguments = $"-cp \"{classpath}\" project.Project";
                startInfo.UseShellExecute = false;
                startInfo.CreateNoWindow = true; // Prevents command window creation

                Process.Start(startInfo);
            }
            catch (Exception ex)
            {
                // Write failure to file if we can't show visual alert
                File.WriteAllText("launcher_error.txt", "Failed to launch game: " + ex.ToString());
            }
        }
    }
}
