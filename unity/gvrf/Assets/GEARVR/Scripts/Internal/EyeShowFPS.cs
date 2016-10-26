using UnityEngine;
using System.Collections;
using UnityEngine.UI;

public class EyeShowFPS : MonoBehaviour {

	public EyeShowFPS() {
		sInstance = this;
	}

    Text text;

    // Use this for initialization
    void Start () {
		text = GetComponent<Text> ();
    }
	
	// Update is called once per frame
	void Update () {
	
	}

    public void setFPS(float fps)
    {
		text.text = ((int)(fps + .5)).ToString();
    }

    private static EyeShowFPS sInstance;

    public static EyeShowFPS Instance
    {
        get
        {
            return sInstance;
        }
    }
}
