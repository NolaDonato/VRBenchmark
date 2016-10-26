using UnityEngine;
using System.Collections;

public class AddCamera : MonoBehaviour {
	public float positionX;
	public float positionY;
	public float positionZ;
	public GameObject cameraPrefab;

	// Use this for initialization
	void Start () {
		// create a camera
		GameObject cam = Instantiate(cameraPrefab);
		cam.name = cam.name.Replace("(Clone)","");
		cam.transform.parent = this.gameObject.transform.parent;
		cam.transform.position = new Vector3(positionX, positionY, positionZ);
		//cam.GetComponent<Camera> ().enabled = true;
	}

	// Update is called once per frame
	void Update () {

	}
}
